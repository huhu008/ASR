
import os
import requests
FILE_FOLDER = "C:/Users/yzkj/Desktop/test/"
def eachFile(filepath):
    pathDir = os.listdir(filepath)
    arr = []
    for allDir in pathDir:
        child = os.path.join('%s%s' % (filepath, allDir))
        print(child)
        suffix = os.path.splitext(child)[-1]
        if suffix != ".txt":
            url = 'http://localhost:8080/file'
            files = {'file': open(child, 'rb')}
            requests.post(url, files=files)
            r = requests.get("http://localhost:8080/file")
            print(r.text)
    return arr
if __name__ == '__main__':
    print(eachFile(FILE_FOLDER))

