DHT = require("dht_lib")

trig = 5 --IO14
echo = 1 --IO5
dht = 2 --IO4
led = 4 --IO2

pulse_time = 0
trash_level = -1
height_to_full = 50
height_to_sensor = 60
client_connected = false

m = mqtt.Client("ESP8266-" .. node.chipid(), 120, "${DEVICE_TOKEN}", "")

function save_config()
    file.open("config", "w+")
    file.writeline(height_to_full .. "," .. height_to_sensor)
    file.close()
    print("Configs saved")
end

function read_config()
    if (file.open("config") ~= nil) then
        local result = string.sub(file.readline(), 1, -2) -- to remove newline character
        file.close()
        local v1, v2 = result:match("([^,]+),([^,]+)")
        height_to_full = tonumber(v1)
        height_to_sensor = tonumber(v2)
        print("Loaded configs:" .. height_to_full .. "," .. height_to_sensor)
    else
        print("Using default configs")
    end
end

gpio.mode(trig, gpio.OUTPUT)
gpio.mode(led, gpio.INPUT)
gpio.mode(echo, gpio.INT)

read_config()

gpio.trig(echo, "both", function(level)
    du = tmr.now() - pulse_time
    if (level == 1) then
        pulse_time = tmr.now()
    else
        -- 1cm ==> 40
        local level = height_to_sensor - (du / 40);
        if (level >= height_to_full) then
            gpio.write(led, gpio.HIGH)
        else
            gpio.write(led, gpio.LOW)
        end
        trash_level = level * 100 / height_to_full
    end
end)

tmr.alarm(0, 5000, 1, function()
    collectgarbage()
    gpio.write(trig, gpio.HIGH)
    tmr.delay(10)
    gpio.write(trig, gpio.LOW)
    if client_connected then
        if (trash_level > -1) then
            DHT.read(dht)
            local t = DHT.getTemperature()
            local h = DHT.getHumidity()
            local payload = "{event:{metaData:{owner:\"${DEVICE_OWNER}\",deviceId:\"${DEVICE_ID}\"},payloadData:{trashlevel:" .. trash_level .. ",temperature:" .. t .. ", humidity:" .. h .. "}}}"
            m:publish("carbon.super/garbagebin/${DEVICE_ID}/data", payload, 0, 0, function(client)
                print("Published> Trash Level: " .. trash_level .. "% Temperature: " .. t .. "C  Humidity: " .. h .. "%")
            end)
        end
    else
        connectMQTTClient()
    end
end)

function connectMQTTClient()
    local ip = wifi.sta.getip()
    if ip == nil then
        print("Waiting for network")
    else
        print("Client IP: " .. ip)
        print("Trying to connect MQTT client")
        m:connect("${MQTT_EP}", ${MQTT_PORT}, 0, function(client)
            client_connected = true
            print("MQTT client connected")
            subscribeToMQTTQueue()
        end)
    end
end

function subscribeToMQTTQueue()
    m:subscribe("carbon.super/garbagebin/${DEVICE_ID}/command", 0, function(client, topic, message)
        print("Subscribed to MQTT Queue")
        gpio.write(trig, gpio.LOW)
    end)
    m:on("message", function(client, topic, message)
        print("MQTT message received")
        print(message)
        local v1, v2= message:match("([^,]+),([^,]+)")
        height_to_full = tonumber(v1)
        height_to_sensor = tonumber(v2)
        print("Loaded configs:" .. height_to_full .. "," .. height_to_sensor)
        save_config();
    end)
    m:on("offline", function(client)
        print("Disconnected")
        client_connected = false
    end)
end
