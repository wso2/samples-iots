-- ESP8266-HTTP Library
-- Written 2014 by Tobias Mädel (t.maedel@alfeld.de)
-- Licensed unter MIT

local moduleName = ... 
local M = {}
_G[moduleName] = M

function M.download(host, port, url, path, callback)
	file.remove(path);
	file.open(path, "w+")

	payloadFound = false
	conn=net.createConnection(net.TCP, false) 
	conn:on("receive", function(conn, payload)

		if (payloadFound == true) then
			file.write(payload)
			file.flush()
		else
			if (string.find(payload,"\r\n\r\n") ~= nil) then
				file.write(string.sub(payload,string.find(payload,"\r\n\r\n") + 4))
				file.flush()
				payloadFound = true
			end
		end

		payload = nil
		collectgarbage()
	end)
	conn:on("disconnection", function(conn) 
		conn = nil
		file.close()
		callback("ok")
	end)
	conn:on("connection", function(conn)
		conn:send("GET /"..url.." HTTP/1.0\r\n"..
			  "Host: "..host.."\r\n"..
			  "Connection: close\r\n"..
			  "Accept-Charset: utf-8\r\n"..
			  "Accept-Encoding: \r\n"..
			  "User-Agent: Mozilla/4.0 (compatible; esp8266 Lua; Windows NT 5.1)\r\n".. 
			  "Accept: */*\r\n\r\n")
	end)
	conn:connect(port,host)
end
return M
