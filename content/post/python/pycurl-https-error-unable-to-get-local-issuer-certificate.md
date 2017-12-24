---
title: 'pycurl https error: unable to get local issuer certificate '
date: 2014-12-28 19:45:33
tags: ["python"]
categories: ["python"]

---
在 pycurl 访问 https 链接时可能会报： pycurl.error: (60, 'SSL certificate problem: unable to get local issuer certificate') 错误<br>
一个比较好的解决方法是：
```python
import pycurl
import certifi

curl = pycurl.Curl()
curl.setopt(pycurl.CAINFO, certifi.where())
curl.setopt(pycurl.URL, 'https://www.quora.com')
curl.perform()
```