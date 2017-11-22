# -*- coding:utf-8 -*-
import os
import requests
import time
from websocket import create_connection
FILE_FOLDER = "C:/Users/59785/Desktop/test/"   #存放pcm的文件夹，文件名不能为中文
def eachFile(filepath):
    pathDir = os.listdir(filepath)
    arr = []
    for allDir in pathDir:
        child = os.path.join('%s%s' % (filepath, allDir))
        print(child)
        suffix = os.path.splitext(child)[-1]
        if suffix != ".txt":
            ws = create_connection("ws://192.168.1.154:8082/FeedAudio",
                                   header=["UUID: dest", "Channels: 1", "Online: True",
                                           "DecodeRecall: http://192.168.1.215:8080/yy/getJson",
                                           ])
            data = open(child, "rb").read()
            ws.send(data)
            ws.send_close()
            time.sleep(1)
            ws.close()
            url = "http://192.168.1.215:8080/yy/file"
            files = {'file': open(child, 'rb')}
            requests.post(url, files=files)
            r = requests.get("http://192.168.1.215:8080/yy/file")
            arr.append(r.json())
    return arr

if __name__ == '__main__':
    print(eachFile(FILE_FOLDER))

# sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
# sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
# sock.bind("0.0.0.0:port_num")
# sock.listen(1)
# print("listener started")
# client, cltadd = sock.accept()
# print("accept a connect")
#
# ws2 = create_connection("ws:http://0.0.0.0:port_num")
# result = ws2.recv()
# print(result)
#
# ws = create_connection("ws:http://localhost:8080/getServer")
# ws2 = create_connection("ws:http://localhost:8080/getServer2")
# ws.send("nihao ")
# ws2.send("nihao 2")
# print(ws.recv())
# print(ws.recv())
# print(ws.recv())
# print(ws.recv())
# print(ws2.recv())
# print(ws2.recv())
# print(ws2.recv())
# print(ws2.recv())
# ws.close()
# ws2.close()