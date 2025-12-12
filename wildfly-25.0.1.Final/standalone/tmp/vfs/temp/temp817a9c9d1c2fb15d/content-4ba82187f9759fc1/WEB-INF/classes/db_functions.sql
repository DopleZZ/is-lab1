-- SQL функции для специальных операций

-- 1. Вернуть количество объектов, значение поля fuelConsumption которых равно заданному
CREATE OR REPLACE FUNCTION count_vehicles_by_fuel_consumption(fuel_consumption_param FLOAT)
RETURNS BIGINT AS $$
BEGIN
    RETURN (SELECT COUNT(*) FROM vehicles WHERE fuel_consumption = fuel_consumption_param);
END;
$$ LANGUAGE plpgsql;

-- 2. Вернуть массив объектов, значение поля name которых начинается с заданной подстроки
CREATE OR REPLACE FUNCTION find_vehicles_by_name_prefix(prefix_param VARCHAR)
RETURNS TABLE (
    id INTEGER,
    name VARCHAR,
    coordinates_id BIGINT,
    creation_date TIMESTAMP,
    vehicle_type VARCHAR,
    engine_power INTEGER,
    number_of_wheels BIGINT,
    capacity BIGINT,
    distance_travelled FLOAT,
    fuel_consumption FLOAT,
    fuel_type VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT v.id, v.name, v.coordinates_id, v.creation_date, v.vehicle_type,
           v.engine_power, v.number_of_wheels, v.capacity, v.distance_travelled,
           v.fuel_consumption, v.fuel_type
    FROM vehicles v
    WHERE v.name LIKE prefix_param || '%';
END;
$$ LANGUAGE plpgsql;

-- 3. Вернуть массив объектов, значение поля fuelType которых меньше заданного
CREATE OR REPLACE FUNCTION find_vehicles_by_fuel_type_less(fuel_type_param VARCHAR)
RETURNS TABLE (
    id INTEGER,
    name VARCHAR,
    coordinates_id BIGINT,
    creation_date TIMESTAMP,
    vehicle_type VARCHAR,
    engine_power INTEGER,
    number_of_wheels BIGINT,
    capacity BIGINT,
    distance_travelled FLOAT,
    fuel_consumption FLOAT,
    fuel_type VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT v.id, v.name, v.coordinates_id, v.creation_date, v.vehicle_type,
           v.engine_power, v.number_of_wheels, v.capacity, v.distance_travelled,
           v.fuel_consumption, v.fuel_type
    FROM vehicles v
    WHERE v.fuel_type < fuel_type_param
    ORDER BY v.fuel_type;
END;
$$ LANGUAGE plpgsql;

-- 4. "Скрутить" счётчик пробега транспортного средства с заданным id до нуля
CREATE OR REPLACE FUNCTION reset_distance_travelled(vehicle_id INTEGER)
RETURNS VOID AS $$
BEGIN
    UPDATE vehicles
    SET distance_travelled = 0
    WHERE id = vehicle_id;
    
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Vehicle with id % not found', vehicle_id;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- 5. Добавить транспортному средству с заданным id указанное число колёс
CREATE OR REPLACE FUNCTION add_wheels_to_vehicle(vehicle_id INTEGER, wheels_to_add BIGINT)
RETURNS VOID AS $$
BEGIN
    UPDATE vehicles
    SET number_of_wheels = number_of_wheels + wheels_to_add
    WHERE id = vehicle_id;
    
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Vehicle with id % not found', vehicle_id;
    END IF;
END;
$$ LANGUAGE plpgsql;

