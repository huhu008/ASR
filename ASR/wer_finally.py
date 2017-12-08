import sys
import os
import requests
import ssl
import json
import re
import numpy
from websocket import create_connection


def asr_dir(asr_type):
    print('asr')
    fout = open(asr_type+"/result.txt", "w+")
    path_dir = os.listdir(asr_type)
    recognization_list = []
    count = 0
    for all_dir in path_dir:
        recognization = dict([("result", []), ("filename", all_dir)])
        child = os.path.join(asr_type, all_dir)
        suffix = os.path.splitext(child)[-1]
        if suffix != ".txt":
            count +=1
            print("%s      %d   " % (child, count))
            result_txt = child[:-4]+".txt"
            result = open(result_txt, "rU").read()
            result = result.replace('\n', '')
            result = re.sub(str("[\.\!\/_,$%^*(+?\"\']+|[+——！，。？、~@#￥%……&*（）]+"), str(""), str(str(result)))
            fout.write("%s[%s]  %d\n" % (child, result,count))
            # print(result)
            recognization_others = asr_others(child).get("result")
            for each_engine in recognization_others:
                sentence = each_engine.get("sentence")
                sentence = re.sub(str("[\.\!\/_,$%^*(+?\"\']+|[+——！，。？、~@#￥%……&*（）]+"), str(""), str(str(sentence)))
                # print(sentence)
                worldError = wer(sentence, result)
                each_engine.update(dict([("worldError", worldError), ("worldSum", len(sentence))]))
                recognization["result"].append(each_engine)
            if "y" in sys.argv[3] or "Y" in sys.argv[3] or sys.argv[3] == "all":
                recognization_yuzhi = asr_yuzhi(child)
                sentence = recognization_yuzhi.get("sentence")
                sentence = re.sub(str("[\.\!\/_,$%^*(+?\"\']+|[+——！，。？、~@#￥%……&*（）]+"), str(""), str(str(sentence)))
                worldError = wer(result,sentence)
                recognization_yuzhi.update(dict([("worldError", worldError), ("worldSum", len(sentence))]))
                recognization["result"].append(recognization_yuzhi)
            for each_engine in recognization.get("result"):
                fout.write("%s:  %s  worldError: %d\n" % (each_engine.get("engine"), each_engine.get("sentence"), each_engine.get("worldError")))
            recognization_list.append(recognization)
            if count % 20 == 0:
                asrResult = worldErrorRate(recognization_list)
                for key, value in asrResult.items():
                    fout.write("%s   %s \n" % (key, value))
    fout.close()


def asr_wavlist(asr_type):
    print("it is a wavlist")
    wavlist = open(asr_type, "rU")
    fout = open("result.txt", "w")
    recognization_list = []
    count = 0
    for line in wavlist:
        count += 1
        strlist = line.split(" ")
        filename = strlist[0]
        print("%s      %d   " % (filename, count))
        recognization = dict([("result", []), ("filename", filename)])
        result = ""
        for i in strlist[1:]:
            result = result + i
        result = result.replace('\n', '')
        fout.write("%s[%s]  %d \n" % (filename, result, count))
        recognization_others = asr_others(filename).get("result")
        for each_engine in recognization_others:
            sentence = each_engine.get("sentence")
            sentence = re.sub(str("[\.\!\/_,$%^*(+?\"\']+|[+——！，。？、~@#￥%……&*（）]+"), str(""), str(str(sentence)))
            # print(sentence)
            worldError = wer(sentence, result)
            each_engine.update(dict([("worldError", worldError), ("worldSum", len(sentence))]))
            recognization["result"].append(each_engine)
        if "y" in sys.argv[3] or "Y" in sys.argv[3] or sys.argv[3] == "all":
            recognization_yuzhi = asr_yuzhi(filename)
            sentence = recognization_yuzhi.get("sentence")
            sentence = re.sub(str("[\.\!\/_,$%^*(+?\"\']+|[+——！，。？、~@#￥%……&*（）]+"), str(""), str(str(sentence)))
            worldError = wer(result, sentence)
            recognization_yuzhi.update(dict([("worldError", worldError), ("worldSum", len(sentence))]))
            recognization["result"].append(recognization_yuzhi)
        for each_engine in recognization.get("result"):
            fout.write("%s:  %s  worldError: %d\n" % (each_engine.get("engine"), each_engine.get("sentence"), each_engine.get("worldError")))
        recognization_list.append(recognization)
        if count % 20 == 0:
            asrResult = worldErrorRate(recognization_list)
            for key, value in asrResult.items():
                fout.write("%s   %s \n" % (key, value))
    fout.close()


def worldErrorRate(recognization_list):
    resultType = dict([("worldSum", 0), ("worldError", 0), ("sentenceSum", 0), ("sentenceError", 0), ("worldErrorRate", 0), ("sentenceErrorRate", 0)])
    asrResult = {}
    for each_recognization in recognization_list:
        result = each_recognization.get("result")
        for each_engine in result:
            engine = each_engine.get("engine")
            if engine not in asrResult:
                asrResult[engine] = resultType.copy()
            asrResult[engine]["worldError"] += each_engine.get("worldError")
            asrResult[engine]["worldSum"] += each_engine.get("worldSum")
            asrResult[engine]["sentenceSum"] += 1
            if each_engine.get("worldError") > 0:
                asrResult[engine]["sentenceError"] += 1
            asrResult[engine]["worldErrorRate"] = str(asrResult[engine]["worldError"] * 100 / asrResult[engine]["worldSum"]) + "%"
            asrResult[engine]["sentenceErrorRate"] = str(asrResult[engine]["sentenceError"] * 100 / asrResult[engine]["sentenceSum"]) + "%"
    return asrResult


def asr_yuzhi(filename):
    url = "wss://asr.yuzhix.com:8443/websocket?" \
          "access_id=test" \
          "&nonce_str=123456" \
          "&uuid=test_TEST123" \
          "&sign=246a807d21d7ab41412c0a9c5e4b7faddefc126dd7f2eb0677d43ecaf773cb97"
    ws = create_connection(url=url,
                           sslopt={"cert_reqs": ssl.CERT_NONE},
                           header=["uuid:test_TEST123", "channels:1", "online:true"])
    data = open(filename, "rb").read()
    ws.send(data, opcode=0x2)
    yuzhi = json.loads(ws.recv())
    yuzhi_result = {
        "engine": "yuzhi",
        "sentence": yuzhi.get("decode_result"),
        "time": (int(yuzhi.get("decode_time")[-8:]) - int(yuzhi.get("recv_time")[-8:])) // 1000
    }
    return yuzhi_result


def asr_others(filename):
    url = sys.argv[2]+"/file"
    data = {
        "engine": sys.argv[3]
    }
    files = {
        "file": (open(filename, "rb"))
    }
    requests.post(url=url, data=data, files=files)
    r = requests.get(url, data)
    return r.json()


def wer(r, h):
    """
    This is a function that calculate the word error rate in ASR.
    You can use it like this: wer("what is it".split(), "what is".split())
    """
    #build the matrix
    d = numpy.zeros((len(r)+1)*(len(h)+1), dtype=numpy.uint8).reshape((len(r)+1, len(h)+1))
    for i in range(len(r)+1):
        for j in range(len(h)+1):
            if i == 0: d[0][j] = j
            elif j == 0: d[i][0] = i
    for i in range(1, len(r)+1):
        for j in range(1, len(h)+1):
            if r[i-1] == h[j-1]:
                d[i][j] = d[i-1][j-1]
            else:
                substitute = d[i-1][j-1] + 1
                insert = d[i][j-1] + 1
                delete = d[i-1][j] + 1
                d[i][j] = min(substitute, insert, delete)
    result = float(d[len(r)][len(h)]) / len(r) * 100
    result = str("%.2f" % result) + "%"
    return d[len(r)][len(h)]


if __name__ == "__main__":
    print(sys.argv)
    asr_type = sys.argv[1]
    asr_address = sys.argv[2]
    asr_engine = sys.argv[3]
    if os.path.isdir(asr_type):
        print("is a dir")
        asr_dir(asr_type)
    elif os.path.isfile(asr_type):
        print("is a file")
        asr_wavlist(asr_type)
    else:
        print("it is a special file (socket, FIFO, device file)")

