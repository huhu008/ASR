#-*- coding: utf-8 -*-
#!/usr/bin/env python

import sys
import numpy
import re
import time
import requests
import os
from websocket import create_connection
from string import punctuation

def asrwer(foldername, referenceFolder = None):
    #if (referenceFolder == None):
    refF = foldername
    #else:
    #    refF = referenceFolder
    fout = open(foldername+"result.txt", "w")
    pathDir =os.listdir(foldername)
    result = dict([])
    resultType = dict([('wordSum', 0), ('wordError', 0), ('sentenceSum', 0), ('sentenceError', 0)])
    asrResult = {}
    zhPattern = re.compile(u'[\u4e00-\u9fa5]+')
    recognization = asr(foldername)
    for line in recognization:
        result = line.get('result', None)
        labelfile = line.get('filename', None)
        fout.write("%s:" % labelfile)
        if labelfile is not None:
            labelfile = labelfile+'.txt'
        if result is None or labelfile is None:
            break
        address = open(refF+'/'+labelfile, "rU")

        try:
            reference = address.read()
            reference = reference.replace('\n', '')
            reference = re.sub(str("[\.\!\/_,$%^*(+?\"\']+|[+——！，。？、~@#￥%……&*（）]+"), str(""), str(str(reference)))
            if zhPattern.search(reference):
                reference = reference.replace(' ', '')
            else:
                reference = reference.split()
            fout.write("%s " % reference)
            for eachEngine in result:
                engineName = eachEngine.get("engine", None)
                hypothesis = eachEngine.get("sentence", None)
                if engineName is not None and hypothesis is not None:
                    if engineName not in asrResult:
                        asrResult[engineName] = resultType.copy()
                    hypothesis = re.sub(str("[\.\!\/_,$%^*(+?\"\']+|[+——！，。？、~@#￥%……&*（）]+"), str(""), hypothesis)
                    if not zhPattern.search(hypothesis):
                        hypothesis = hypothesis.split()
                    else:
                        hypothesis = hypothesis.replace(' ', '')
                    errorNum = wer(reference, hypothesis)
                    asrResult[engineName]['wordSum'] += len(reference)
                    asrResult[engineName]['wordError'] += wer(reference, hypothesis)
                    asrResult[engineName]['sentenceSum'] += 1
                    if errorNum > 0:
                        asrResult[engineName]['sentenceError'] += 1
                    fout.write("engine:%s  result:%s error:%d " % (engineName, hypothesis, errorNum))
            
        finally:
            address.close()
        fout.write("\n")
    for eachEngine in asrResult:
        wordErrorRate = float(asrResult[eachEngine]['wordError']) * 100 / asrResult[eachEngine]['wordSum']
        sentenceErrorRate = float(asrResult[eachEngine]['sentenceError']) * 100 / asrResult[eachEngine]['sentenceSum']
        asrResult[eachEngine]['wordErrorRate'] = str("%.2f" % wordErrorRate) + "%"
        asrResult[eachEngine]['sentenceErrorRate'] = str("%.2f" % sentenceErrorRate) + "%"
        fout.write("engine:%s  wordErrorRate:%0.2f%%  sentenceErrorRate:%0.2f%%\n" % (eachEngine, wordErrorRate, sentenceErrorRate))
    # print(asrResult, file = fout)
    print(asrResult)
    fout.close()
    return asrResult


def asr(filepath):
    pathDir = os.listdir(filepath)
    arr = []
    count = 0
    for allDir in pathDir:
        bt = time.time()
        child = os.path.join('%s%s' % (filepath, allDir))
        suffix = os.path.splitext(child)[-1]
        if suffix != ".txt":
            count += 1
            print(child + "   ", count)

            ws = create_connection("ws://192.168.1.154:8090/FeedAudio",
                                   header=["UUID:" + allDir, "Channels: 1", "Online: True",
                                           "DecodeRecall:"+sys.argv[2]+"/getJson",
                                           ])
            data = open(child, "rb").read()
            ws.send(data)
            ws.send_close()
            time.sleep(0.05)
            ws.close()
            url = sys.argv[2]+'/file'
            data = {
                'engine': sys.argv[3]
            }
            file = {
                'file': (open(child, 'rb'))
            }
            requests.post(url, data=data, files=file)
            r = requests.get(url, data)
            et = time.time()
            times = et - bt
            print(times)
            arr.append(r.json())
    return arr


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
    #find out the manipulation steps


if __name__ == '__main__':
    filename1 = sys.argv[1]
    asrwer(filename1)
