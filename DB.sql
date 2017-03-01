/* 
	Файл структуры БД 
	СУБД = PostgreSQL
*/


-- Создали БД
CREATE DATABASE "LSH" WITH ENCODING='UTF8' CONNECTION LIMIT=2; 
-- Одно подключение для сервера Администрирования
-- Другое для самого приложения


-- Таблица пользователей
CREATE TABLE users (
	id SERIAL CONSTRAINT uses_pk PRIMARY KEY, -- id
	login VARCHAR(128), -- Логин пользователя
	password VARCHAR(32) -- Пароль на ссылку - хэш
);

INSERT INTO users(id) VALUES (0); -- Дефолтный логин - для ссылок без владельца


-- Таблица для сокращенных ссылок
CREATE TABLE short (
	id SERIAL CONSTRAINT short_pk PRIMARY KEY, -- Внутрениий id
	user_id BIGINT NOT NULL, -- id записи пользователя - повторяемый. Выдаеться заново, после того как запись становиться не валидной
	-- когда действие ссылки кончилось, И\ИЛИ колво переходов превышено
	link VARCHAR(1024), -- Оригинальная ссылка, приведенная к виду www.site.zone
	create_date TIMESTAMP WITH TIME ZONE DEFAULT current_timestamp, -- Время создания
	expired_date TIMESTAMP WITH TIME ZONE, -- До какого времени ссылка рабочая
	max_count INT, -- Максимальное кол-во переходов - если 0, то бесконечно
	current_count INT, -- Текущее кол-во переходов
	password VARCHAR(32), -- Пароль на ссылку - хэш
	ip CIDR, -- IP откуда создали
	user_agent VARCHAR(255), -- Браузер, откуда создали
	owner INT REFERENCES users(id) ON UPDATE CASCADE DEFAULT 0 -- Владелец ссылки
);


-- Таблица для аналитики - отслеживание переходов по ссылкам
CREATE TABLE analitics (
	id SERIAL CONSTRAINT analitics_pk PRIMARY KEY, -- Внутрениий id
	short_id INT NOT NULL REFERENCES short(id) ON UPDATE CASCADE, -- Запись перехода 
	visit_time TIMESTAMP WITH TIME ZONE DEFAULT current_timestamp, -- Время перехода
	ip CIDR, -- IP откуда пришли
	user_agent VARCHAR(255) -- Браузер, откуда пришли
);


-- Таблица статусов user_id - простейшая Key-Value. Может в редис вынести?
CREATE TABLE status (
	user_id BIGINT CONSTRAINT status_pk PRIMARY KEY, -- id пользователя
	valid BOOLEAN -- Его статус
);


-- Триггерная функция, обновляющая таблицу статуса, при вставке нового значения в таблицу short
CREATE FUNCTION shortTGSetStatus () 
RETURNS trigger AS
$$
	BEGIN
		IF EXISTS (SELECT valid FROM status WHERE user_id = NEW.user_id)
		THEN
			UPDATE status SET valid = true WHERE user_id = NEW.user_id;
		ELSE
			INSERT INTO status(user_id, valid) VALUES (NEW.user_id, true);
		END IF;
	RETURN NEW;
	END;
$$ 
LANGUAGE plpgsql;


-- Триггер на вставку данных в таблицу short
CREATE TRIGGER shortTG AFTER INSERT ON short FOR EACH ROW EXECUTE PROCEDURE shortTGSetStatus();


-- Индекс на user_id
CREATE INDEX user_id_index ON short(user_id);
CREATE INDEX analitics_user_id_index ON analitics(short_id);


-- Последовательность user_id
CREATE SEQUENCE usr START 1;


-- Функция, запускающаяся по времени и инвалидирующая все записи, где время вышло 
-- НЕИМОВЕРНО ДОЛГО!
CREATE FUNCTION invalidate ()
RETURNS VOID AS  
$$
BEGIN

	-- Логика - мы должны получить набор user_id из статуса, где статус валиден. Затем, пойти по всем этим id в таблице short
	-- С конца по одному (у нас может быть несколько строчек с одинаковым user_id, нам нужна только последняя - первая с конца) 
	-- И изменить таблицу status по некому правилу (дата истекла, кол-во переходов больше)

	-- 3 подзапроса(каскадно) в одном запросе. Да ешё и с оконной функцией! Рекорд! 
	UPDATE status SET valid = FALSE WHERE user_id IN ( -- Обновляем статус на невалидный где id в

		SELECT user_id FROM short WHERE id in ( -- Выбрали все не валидные user_id по условию

			SELECT id FROM ( 

				SELECT row_number() over(PARTITION BY user_id ORDER BY id DESC) AS c, id, user_id FROM short  -- Получили другие данные из short с номерами строк у столбца user_id
				WHERE user_id IN (
					SELECT user_id FROM status WHERE valid = true -- Получили набор валидных user_id
				) 

			) t WHERE c = 1 -- Этим мы выбираем первые уникальные c конца - см выше

		) AND ( -- и где
			max_count != 0 -- кол-во переходов не бесконечно
			AND current_count >= max_count -- и кол-во переходов превышена
			OR expired_date < current_timestamp -- или где дата уже истекла
		) 
	);

END
$$ 
LANGUAGE plpgsql;


-- Функция возвращая новый user_id
CREATE FUNCTION get_next_id () 
RETURNS BIGINT AS 
$$
DECLARE ret BIGINT;
DECLARE rett BIGINT;
BEGIN

	-- Вызвали функцию инвалидации
	PERFORM invalidate(); 

	-- Ищем свободные user_id
	SELECT user_id INTO ret
	FROM status
	WHERE valid = FALSE
	LIMIT 1; 
	
	-- Получили текущее значение последовательности
	SELECT setval('usr', nextval('usr')-1) INTO rett;

	-- Если первый свободный дальше, чем значение последовательности
	IF ret > rett THEN 
		ret = NULL; -- То пойдем к следующему условию
	END IF;

	-- Если нет свободных id, генерируем новое
	IF ret IS NULL THEN
		ret = nextval('usr');
	END IF;

	RETURN ret;
END
$$ 
LANGUAGE plpgsql;


-- Создание пользователя для приложения и администрирования
CREATE ROLE "LSH" LOGIN ENCRYPTED PASSWORD 'md5db253021ec23d154c76e692c9d5f0abf' VALID UNTIL 'infinity' CONNECTION LIMIT 2;


-- Разрешения. Владелец - POSTGRES, но LSH может SELECT, INSERT, EXECUTE, UPDATE
GRANT EXECUTE ON FUNCTION invalidate() to "LSH";
GRANT EXECUTE ON FUNCTION get_next_id() to "LSH";

GRANT USAGE, SELECT ON SEQUENCE analitics_id_seq TO "LSH";
GRANT USAGE, SELECT ON SEQUENCE short_id_seq TO "LSH";
GRANT USAGE, SELECT ON SEQUENCE users_id_seq TO "LSH";
GRANT USAGE, SELECT ON SEQUENCE usr TO "LSH";

GRANT SELECT, INSERT ON users TO "LSH";
GRANT SELECT, INSERT, UPDATE ON short TO "LSH";
GRANT SELECT, INSERT ON analitics TO "LSH";
GRANT SELECT, INSERT, UPDATE ON status TO "LSH";
