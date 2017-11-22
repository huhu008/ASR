#-*- coding: utf-8 -*-
#!/usr/bin/env python

import sys
import numpy
import re
import json
import os
from string import punctuation
import asr


def asrwer(foldername, referenceFolder = None):
    #if (referenceFolder == None):
    refF = foldername
    #else:
    #    refF = referenceFolder
    fout = open(foldername+"result.txt","w")
    pathDir =  os.listdir(foldername)
    result = dict([])
    resultType = dict([('wordSum',0),('wordError',0),('sentenceSum',0),('sentenceError',0)])
    asrResult = {}
    zhPattern = re.compile(u'[\u4e00-\u9fa5]+')
    recognization = asr.eachFile(foldername)
    for line in recognization:
        result = line.get('result', None)
        labelfile = line.get('filename', None)
        fout.write("%s:" % labelfile)
        if labelfile <> None:
            labelfile = labelfile+'.txt'
        if (result == None or labelfile == None):
            break
        address = open(refF+'\\'+labelfile, "rU")
        try:
            reference = address.read()
            reference = reference.replace('\n', '')
            reference = re.sub(unicode("[\.\!\/_,$%^*(+?\"\']+|[+——！，。？、~@#￥%……&*（）]+","utf8"), unicode("","utf8"), unicode(str(reference),'gb2312'))
            if zhPattern.search(reference):
                reference = reference.replace(' ','')
            else:
                reference = reference.split()
            fout.write("%s " % reference)
            for eachEngine in result:
                engineName = eachEngine.get("engine", None)
                hypothesis = eachEngine.get("sentence", None)
                if (engineName <> None and hypothesis <> None):
                    if (not asrResult.has_key(engineName)):
                        asrResult[engineName] = resultType.copy();
                    hypothesis = re.sub(unicode("[\.\!\/_,$%^*(+?\"\']+|[+——！，。？、~@#￥%……&*（）]+","utf8"), unicode("","utf8"), hypothesis)
                    if not zhPattern.search(hypothesis):
                        hypothesis = hypothesis.split()
                    else:
                        hypothesis = hypothesis.replace(' ','')
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
        
    print(asrResult)
    fout.close()
    return asrResult
        


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
    for i in range(1,len(r)+1):
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
    x = len(r)
    y = len(h)
    list = []
    while True:
        if x == 0 and y == 0: 
            break
        else:
            if d[x][y] == d[x-1][y-1] and r[x-1] == h[y-1]: 
                list.append("e")
                x = x-1
                y = y-1
            elif d[x][y] == d[x][y-1]+1:
                list.append("i")
                x = x
                y = y-1
            elif d[x][y] == d[x-1][y-1]+1:
                list.append("s")
                x = x-1
                y = y-1
            else:
                list.append("d")
                x = x-1
                y = y
    list = list[::-1]

    #print the result in aligned way
    print "REF:",
    for i in range(len(list)):
        if list[i] == "i":
            count = 0
            for j in range(i):
                if list[j] == "d":
                    count += 1;
            index = i - count
            print " "*(len(h[index])),
        elif list[i] == "s":
            count1 = 0
            for j in range(i):
                if list[j] == "i":
                    count1 += 1;
            index1 = i - count1
            count2 = 0
            for j in range(i):
                if list[j] == "d":
                    count2 += 1;
            index2 = i - count2
            if len(r[index1])<len(h[index2]):
                print r[index1]+" "*(len(h[index2])-len(r[index1])),
            else:
                print r[index1],
        else:
            count = 0
            for j in range(i):
                if list[j] == "i":
                    count += 1;
            index = i - count
            print r[index],
    print
    print "HYP:",
    for i in range(len(list)):
        if list[i] == "d":
            count = 0
            for j in range(i):
                if list[j] == "i":
                    count += 1;
            index = i - count
            print " "*(len(r[index])),
        elif list[i] == "s":
            count1 = 0
            for j in range(i):
                if list[j] == "i":
                    count1 += 1;
            index1 = i - count1
            count2 = 0
            for j in range(i):
                if list[j] == "d":
                    count2 += 1;
            index2 = i - count2
            if len(r[index1])>len(h[index2]):
                print h[index2]+" "*(len(r[index1])-len(h[index2])),
            else:
                print h[index2],
        else:
            count = 0
            for j in range(i):
                if list[j] == "d":
                    count += 1;
            index = i - count
            print h[index],
    print
    print "EVA:",
    for i in range(len(list)):
        if list[i] == "d":
            count = 0
            for j in range(i):
                if list[j] == "i":
                    count += 1;
            index = i - count
            print "D"+" "*(len(r[index])-1),
        elif list[i] == "i":
            count = 0
            for j in range(i):
                if list[j] == "d":
                    count += 1;
            index = i - count
            print "I"+" "*(len(h[index])-1),
        elif list[i] == "s":
            count1 = 0
            for j in range(i):
                if list[j] == "i":
                    count1 += 1;
            index1 = i - count1
            count2 = 0
            for j in range(i):
                if list[j] == "d":
                    count2 += 1;
            index2 = i - count2
            if len(r[index1])>len(h[index2]):
                print "S"+" "*(len(r[index1])-1),
            else:
                print "S"+" "*(len(h[index2])-1),
        else:
            count = 0
            for j in range(i):
                if list[j] == "i":
                    count += 1;
            index = i - count
            print " "*(len(r[index])),
    print
    print "WER: "+result
    
    return d[len(r)][len(h)]

if __name__ == '__main__':
    import sys
    reload(sys)
    sys.setdefaultencoding( "utf-8" )
    filename1 = sys.argv[1]
    asrwer(filename1)
