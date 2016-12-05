/* 
	Файл структуры БД 
	СУБД = PostgreSQL
*/

-- TODO Вносить создание ссылки данные в аналитику

-- Создание пользователя для приложения
CREATE ROLE "LSH" LOGIN ENCRYPTED PASSWORD 'md5db253021ec23d154c76e692c9d5f0abf' VALID UNTIL 'infinity' CONNECTION LIMIT 1;

-- Создали БД
CREATE DATABASE "LSH" WITH ENCODING='UTF8' OWNER="LSH" CONNECTION LIMIT=-1;

-- Таблица для сокращенных ссылок
CREATE TABLE short (
	id SERIAL CONSTRAINT shot_pk PRIMARY KEY, -- Внутрениий id
	user_id INT NOT NULL, -- id записи пользователя - повторяемый. Выдаеться заново, после того как запись становиться не валидной
	-- когда действие ссылки кончилось, И\ИЛИ колво переходов превышено
	link VARCHAR(1024), -- Оригинальная ссылка, приведенная к виду www.site.zone
	expired_date TIMESTAMP WITH TIME ZONE, -- До какого времени ссылка рабочая
	max_count INT, -- Максимальное кол-во переходов - если 0, то бесконечно
	current_count INT -- Текущее кол-во переходов
);


-- Таблица для аналитики - отслеживание переходов по ссылкам
CREATE TABLE analitics (
	id SERIAL CONSTRAINT analyze_pk PRIMARY KEY, -- Внутрениий id
	short_id INT NOT NULL REFERENCES short(id) ON UPDATE CASCADE, -- Запись перехода 
	visit_time TIMESTAMP WITH TIME ZONE DEFAULT current_timestamp, -- Время перехода
	ip CIDR, -- IP откуда пришли
	user_agent VARCHAR(180) -- Браузер, откуда пришли
);


-- Таблица статусов user_id - простоейшая Key-Value. Может в редис вынести?
CREATE TABLE status (
	user_id INT CONSTRAINT status_pk PRIMARY KEY, -- id пользователя
	valid BOOLEAN -- Его статус
);


-- Индекс на user_id
CREATE INDEX user_id_index ON short (user_id);


-- Последовательность user_id
CREATE SEQUENCE usr START 1;


-- Функция возвращая новый user_id
CREATE OR REPLACE FUNCTION get_next_id () 
RETURNS INT AS 
$$
DECLARE ret INT;
BEGIN
	-- Ищем свободные user_id
	SELECT user_id INTO ret
	FROM status
	WHERE valid = FALSE
	LIMIT 1; -- TODO - проверить, что возращает первый сверху

	-- Если нет свободных id, генерируем новое
	IF ret IS NULL THEN
		ret = nextval('usr');
	END IF;

	RETURN ret;
END
$$ 
LANGUAGE plpgsql;


-- Функция, возращающая набор user_id, которые валидные
CREATE OR REPLACE FUNCTION calc_valid_id ()
RETURNS INT[] AS  
$$
DECLARE t INT; -- Переменная в цикле
DECLARE max INT; -- Кол-во шагов в цикле
DECLARE a INT; -- Переменная счетчик в массиве
DECLARE res INT[]; -- Массив id
BEGIN

	a = 1;
	max = ( SELECT COUNT(*) FROM status );
		
	FOR t IN 1..max LOOP -- Идем по всем id

		IF EXISTS ( -- Если есть еще валидный id то добавляем его в массив
 			SELECT user_id FROM short  -- Запрашиваем с конца все user_id где дата еще не истекла с лимитом 1
			WHERE user_id = t
			AND expired_date < current_time
			ORDER BY id DESC
			LIMIT 1 )
		THEN
			-- Добавить user_id в вывод
			res[a] = t;
			a = a + 1;
		END IF;
	
	END LOOP;	

	RETURN res;
END
$$ 
LANGUAGE plpgsql;


-- Функция, запускающаяся по времени и инвалидирующая все записи, где время вышло 
CREATE OR REPLACE FUNCTION check_time_valid ()
RETURNS VOID AS  
$$
BEGIN

	UPDATE status SET valid = FALSE WHERE user_id != ALL (calc_valid_id() );
	-- TODO Проверить что конструция != ALL эквивалентна NOT IN (massive[])

END
$$ 
LANGUAGE plpgsql;


-- Заполнение тестовыми данными
/*
CREATE OR REPLACE FUNCTION genRandText (inp INT) RETURNS TEXT AS
$$
DECLARE t CHAR; 
DECLARE ans TEXT;
BEGIN
	FOR a IN 1..inp LOOP
		t = ( (array['a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','r','s','t','u','v','w','x','y','z','.','1','2','3','4','5','6','7','8','9','0']) [ceil( random() * 27 )] ) ;
		ans = CONCAT (ans, t); -- А вот просто не рабоатет ans || t;
	END LOOP;
RETURN ans;
END
$$
LANGUAGE plpgsql;
*/

--/*
SELECT 
	generate_series(1, 1000),
	generate_series(1, 1000),
	genRandText(20) || '.' || genRandText(3),
	( now() + interval '1 day' * round(random()*200) )::timestamp,
	(random()*20)::int,
	(random()*20)::int;
--*/