#Yuzhi-tools
服务端每次只能供一个人评测

wer_finally.py

[1]python [2]wer_finally.py [3] (1)文件夹路径 或者(2).wavlist   [4]http://192.168.1.133:8080  [5]a,b,x,y


1.python 执行命令

2.要执行的 python 脚本文件名

3.(1)测试集文件夹的绝对路径(其中包含音频文件(.pcm/wav)，和对应的正确结果(.TXT))  测试结果再文件夹下的"result.txt"内
  (2)test.wavlist 为音频文件列表 + 正确的结果  ,识别结果在python执行目录下的"result.txt"内 
   例如：test.wavlist (/Users/wd007/Downloads/aishell_3m/01c622dc-c86c-11e7-96b4-701ce760e9eb.wav  我 又 不 指 望 设 立 信 托 来 赚 钱 )

4.服务器端口号

5.将要使用的测试引擎(all:全部，A/a:阿里，B/b百度，C/c迅飞，D/d语智)




