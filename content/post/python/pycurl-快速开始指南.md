---
title: pycurl 快速开始指南
date: 2014-12-20 11:37:28
categories: ["python"]
tags: ["python"]
---

## 获取网络资源

安装好PycURL之后，我们就可以执行一些网络操作了。最简单的是通过一个网站的URL获取它的相关资源。使用PycURL执行一个网络请求，需要以下步骤：
1. 创建一个pucurl.Curl的实例。
2. 使用  setopt  来设置一些请求选项。
3. 调用 perform 来执行请求。<br>
<!-- more -->

在python2 中，我们采用以下的方法获取网络资源：
``` python
import pycurl
from StringIO import StringIO

buffer = StringIO()
c = pycurl.Curl()
c.setopt(c.URL, 'http://pycurl.sourceforge.net/')
c.setopt(c.WRITEDATA, buffer)
c.perform()
c.close()

body = buffer.getvalue()
# Body is a string in some encoding.
# In Python 2, we can print it without knowing what the encoding is.
print(body)```
PycURL没有对网络的响应提供存贮机制。因此，我们必须提供一个缓存（以StringIO的形式）并且让PycURL将内容写入这个缓存。
现有的大多数PycURL代码使用 WRITEFUNCTION 而不是WRITEDATA：
```python
c.setopt(c.WRITEFUNCTION, buffer.write)
```
虽然 WRITEFUNCTION 还能继续使用，但是没有必要了。因为PycURL 7.19.3版本中的WRITEDATA 可以使用任何具有write方法的Python类。
```python
import pycurl
from io import BytesIO

buffer = BytesIO()
c = pycurl.Curl()
c.setopt(c.URL, 'http://pycurl.sourceforge.net/')
c.setopt(c.WRITEDATA, buffer)
c.perform()
c.close()

body = buffer.getvalue()
# Body is a byte string.
# We have to know the encoding in order to print it to a text file
# such as standard output.
print(body.decode('iso-8859-1'))
```
在Python 3 中，PycURL的响应是字节串。字节串对于我们下载二进制文件是比较方便的，但是我们处理文本内容时必须对字节串解码。
在上面的例子中，我们假设内容是以iso-8859-1编码的。检查响应头在现实中，我们希望使用服务器指定的编码格式对响应解码而不是假设一个编码格式解码。我们需要检查响应头来提取服务器指定的编码格式：
```python
import pycurl
import re
try:
    from io import BytesIO
except ImportError:
    from StringIO import StringIO as BytesIO

headers = {}
def header_function(header_line):
    # HTTP standard specifies that headers are encoded in iso-8859-1.
    # On Python 2, decoding step can be skipped.
    # On Python 3, decoding step is required.
    header_line = header_line.decode('iso-8859-1')

    # Header lines include the first status line (HTTP/1.x ...).
    # We are going to ignore all lines that don't have a colon in them.
    # This will botch headers that are split on multiple lines...
    if ':' not in header_line:
        return

    # Break the header line into header name and value.
    name, value = header_line.split(':', 1)

    # Remove whitespace that may be present.
    # Header lines include the trailing newline, and there may be whitespace
    # around the colon.
    name = name.strip()
    value = value.strip()

    # Header names are case insensitive.
    # Lowercase name here.
    name = name.lower()

    # Now we can actually record the header name and value.
    headers[name] = value

buffer = BytesIO()
c = pycurl.Curl()
c.setopt(c.URL, 'http://pycurl.sourceforge.net')
c.setopt(c.WRITEFUNCTION, buffer.write)
# Set our header function.
c.setopt(c.HEADERFUNCTION, header_function)
c.perform()
c.close()

# Figure out what encoding was sent with the response, if any.
# Check against lowercased header name.
encoding = None
if 'content-type' in headers:
    content_type = headers['content-type'].lower()
    match = re.search('charset=(\S+)', content_type)
    if match:
        encoding = match.group(1)
        print('Decoding using %s' % encoding)
if encoding is None:
    # Default encoding for HTML is iso-8859-1.
    # Other content types may have different default encoding,
    # or in case of binary data, may have no encoding at all.
    encoding = 'iso-8859-1'
    print('Assuming encoding is %s' % encoding)

body = buffer.getvalue()
# Decode using the encoding we figured out.
print(body.decode(encoding))
```
不得不说，完成一个非常简单的提取编码格式的工作需要大量的代码。不幸的是，因为libcurl 限制了分配给响应数据的内存，所以只能依赖我的程序来执行这个枯燥乏味的工作。<br>
写入文件: 如果我们要将响应数据存入文件，只要稍作改变就可以了：
```python
import pycurl

# As long as the file is opened in binary mode, both Python 2 and Python 3
# can write response body to it without decoding.
with open('out.html', 'wb') as f:
    c = pycurl.Curl()
    c.setopt(c.URL, 'http://pycurl.sourceforge.net/')
    c.setopt(c.WRITEDATA, f)
    c.perform()
    c.close()
```
最重要的是以二进制方式打开文件，响应内容不需要编码或者解码就可以直接写入文件。
##跟随重定向
默认情况下，libcurl和PycURL都不会跟随重定向的内容。我们可以通过 setopt 来设置跟随重定向：
```python
import pycurl

c = pycurl.Curl()
# Redirects to https://www.python.org/.
c.setopt(c.URL, 'http://www.python.org/')
# Follow redirect.
c.setopt(c.FOLLOWLOCATION, True)
c.perform()
c.close()
```
正如我们没有设置一个写的回调函数一样，默认的libcurl和PycURL将响应体输出到标准输出上。<br>
（使用Python 3.4.1 报错：pycurl.error: (23, 'Failed writing body (0 != 7219)')）

##设置选项
跟随重定向只是libcurl提供的一个选项。还有好多其他的选项，点击这里查看。除了少数例外，PycURL选项的名字都是从libcurl中通过去掉CURLOPT_前缀得来的。因此CURLOPT_URL就成了简单的URL。
##检测响应
我们已经介绍了检测响应头。其他的响应信息可以通过 getinfo 获得，如下所示：
```python
import pycurl
try:
    from io import BytesIO
except ImportError:
    from StringIO import StringIO as BytesIO

buffer = BytesIO()
c = pycurl.Curl()
c.setopt(c.URL, 'http://pycurl.sourceforge.net/')
c.setopt(c.WRITEDATA, buffer)
c.perform()

# HTTP response code, e.g. 200.
print('Status: %d' % c.getinfo(c.RESPONSE_CODE))
# Elapsed time for the transfer.
print('Status: %f' % c.getinfo(c.TOTAL_TIME))

# getinfo must be called before close.
c.close()
```
在此，我们将响应内容写到缓存，避免在标准输出中输出不感兴趣的内容。
响应信息都在<a href="https://curl.haxx.se/libcurl/c/curl_easy_getinfo.html" target="_blank">libcurl的相关文档</a>上有展示。除了少数例外，PycURL的常量都是通过去掉libcurl常量的前缀 CURLINFO_来命名的。因此CURLINFO_RESPONSE_CODE变为RESPONSE_CODE
##提交数据
使用POSTFIELDS选项来提交数据。提交的数据必须先经过URL编码格式编码：
```python
import pycurl
try:
    # python 3
    from urllib.parse import urlencode
except ImportError:
    # python 2
    from urllib import urlencode

c = pycurl.Curl()
c.setopt(c.URL, 'http://pycurl.sourceforge.net/tests/testpostvars.php')

post_data = {'field': 'value'}
# Form data must be provided already urlencoded.
postfields = urlencode(post_data)
# Sets request method to POST,
# Content-Type header to application/x-www-form-urlencoded
# and data to send in request body.
c.setopt(c.POSTFIELDS, postfields)

c.perform()
c.close()
```
POSTFIELDS自动将HTTP请求方式设置为POST方式。其他的请求方式可以通过CUSTOMREQUEST选项设置：
```python
c.setopt(c.CUSTOMREQUEST, 'PATCH')
```