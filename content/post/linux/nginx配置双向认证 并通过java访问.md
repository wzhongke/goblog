---
title: nginx配置双向认证 并通过java访问
date: 2017-06-18 19:42:25
tags: ["linux"]
categories: ["linux"]
---

最近项目开发中的接口要使用双向认证，因为搭建服务器的方式是nginx+resin，而java的keytool配置nginx的双向认证时，并不好用。所以使用了openssl来生成证书。

## 安装openssl
部分linux系统上已经默认安装了openssl，可以使用`openssl version`来查看机器上是否安装了openssl。如果没有安装，可以执行 `yum install nginx openssl -y` 安装

## 使用脚本生成证书
每次使用命令行安装都是一个巨大的考验，尤其是在输入各种信息的时候。输入错误就要Crtl+c重新来过。因此，将安装过程写成一个脚本，方便安装。
<!-- more -->
1. 在nginx的conf目录下新建一个文件夹：`mkdir openssl && cd openssl `
2. 将shell脚本和下面的配置文件拷贝到openssl目录下，可以根据自己修改sheel脚本中的`SUBJECT`和`CA`；
脚本执行过程中需要输入证书的域名，如果机器没有域名，那么输入ip。如果证书的ip或者域名与输入的内容不一致，是用java访问时，会报错
`javax.net.ssl.SSLPeerUnverifiedException: Certificate for <*.*.*.*> doesn't match any of the subject alternative names: []；`
需要输入两种密码，第一种是server.key的密码，第二种是client.key的密码，改密码要在是用证书时用到，需要记住。

```bash
#!/bin/sh
# create self-signed server certificate:
read -p "请输入证书的域 例如[www.example.com or 192.168.1.52]: " DOMAIN
mkdir newcerts private conf server
# 根据自己的需求，来更改这些内容
SUBJECT="/C=CN/ST=BJ/L=BJ/O=company/OU=company/CN=$DOMAIN"
CA="/C=CN/ST=BJ/L=BJ/O=company/OU=company/CN=$DOMAIN"

echo "创建 CA 根证书..."
echo "生成私钥 key 文件..."
openssl genrsa -out private/ca.key 2048  
echo "生成证书请求 csr 文件..."
openssl req -new -subj $CA -key private/ca.key -out private/ca.csr 

echo "生成凭证 crt 文件..."
openssl x509 -req -days 365 -in private/ca.csr -signkey private/ca.key -out private/ca.crt  

echo "为我们的 key 设置起始序列号和创建 CA 键库..."

rm -rf serial index.txt
echo FACE > serial
touch index.txt

echo "服务器证书的生成..."
openssl genrsa -out server/server.key 2048 
openssl req -new -subj $SUBJECT -key server/server.key -out server/server.csr  

echo "使用我们私有的 CA key 为刚才的 key 签名..."
openssl ca -in server/server.csr -cert private/ca.crt -keyfile private/ca.key -out server/server.crt -config "./openssl.conf" 

echo "客户端证书的生成 * 创建存放 key 的目录 users..."
mkdir users  
echo " 为用户创建一个 key..."
openssl genrsa -des3 -out ./users/client.key 2048 

echo "为 key 创建一个证书签名请求 csr 文件..."
openssl req -new -subj $SUBJECT -key ./users/client.key -out ./users/client.csr  

echo "使用我们私有的 CA key 为刚才的 key 签名..."
openssl ca -in ./users/client.csr -cert ./private/ca.crt -keyfile ./private/ca.key -out ./users/client.crt -config "./openssl.conf" 

echo "将证书转换为大多数浏览器都能识别的 PKCS12 文件..."
openssl pkcs12 -export -clcerts -in ./users/client.crt -inkey ./users/client.key -out ./users/client.p12  

echo "把以上生成的文件copy到nginx conf文件的ssl目录下面，如果ssl目录不存在请创建"
echo "接下请配置nginx.conf操作:"
echo " server {																						"
echo " 			...                                           "
echo "     ssl on;                                        "
echo "     ssl_certificate ca/server/server.crt;                "
echo "     ssl_certificate_key ca/server/server.key;            "
echo "     ssl_client_certificate ca/private/ca.crt;             "
echo "     ssl_verify_client on;                          "
echo "			...                                           "
echo "     }                                               "
echo "使用如下命令重新加载nginx配置"
echo "nginx -s reload"
echo "客户端使用： users/client.p12 和 private/ca.crt"

```
在相同的目录下放置如下配置，命名为openssl.conf：
```conf
[ ca ] 
default_ca      = foo                   # The default ca section 

[ foo ] 
dir            = ./         # top dir  
database       = ./index.txt          # index file.  
new_certs_dir  = ./newcerts           # new certs dir 

certificate    = ./private/ca.crt         # The CA cert  
serial         = ./serial             # serial no file  
private_key    = ./private/ca.key  # CA private key  
RANDFILE       = ./private/.rand      # random number file 

default_days   = 365                     # how long to certify for  
default_crl_days= 30                     # how long before next CRL  
default_md     = sha1                     # message digest method to use  
unique_subject = no                      # Set to 'no' to allow creation of  
                                         # several ctificates with same subject. 
policy         = policy_any              # default policy 

[ policy_any ] 
countryName = match  
stateOrProvinceName = match  
organizationName = match  
organizationalUnitName = match  
localityName            = optional  
commonName              = supplied  
emailAddress            = optional
```
## 配置nginx
修改nginx的配置文件，在server配置如下信息
```bash
ssl_certificate /path/to/openssl/server/server.crt;

ssl_certificate_key /path/to/openssl/server/server.key;

ssl_client_certificate  /path/to/openssl/private/ca.crt;

ssl_verify_client on;    ## 开启客户端的验证

ssl_protocols TLSv1 TLSv1.1 TLSv1.2;

```
## 是用chrome浏览器访问
修改完成后，如果不配置证书，在浏览器中是不能访问的。报错： 400 Bad Request No required SSL certificate was sent
安装两个文件： client.p12和ca.crt，之后能够正常访问
![](https://thumbnail0.baidupcs.com/thumbnail/aa0e5bd8524d12cea6429c15e9682d9f?fid=2318483978-250528-393924946231140&time=1497780000&rt=sh&sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-hdynUkKCDnjIDRkD%2FWFc0V0CQb0%3D&expires=8h&chkv=0&chkbd=0&chkpc=&dp-logid=3914457463978335134&dp-callid=0&size=c710_u400&quality=100&vuk=-&ft=video)

## 使用java完成双向认证
将生成的证书： users/client.p12 和 private/ca.crt 拷贝出来。用java做双向认证时，会使用到这两个文件。
java 双向认证的代码如下：
```bash
private final static String P12_PATH = "client.p12";   //客户端证书路径
private final static String P12_PWD = "12345678"; //客户端证书密码
private final static String CA_PATH = "ca.crt";   // 根证书路径

public static String sslRequestGet(String url) throws Exception {
    SSLContext ctx = getSSLContext(P12_PATH, CA_PATH);
    LayeredConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(ctx);
    HttpGet httpget = new HttpGet(url);
    try (CloseableHttpClient httpClient = HttpClients.custom()
            .setSSLSocketFactory(sslSocketFactory)
            .build();CloseableHttpResponse response = httpClient.execute(httpget) ) {
        HttpEntity entity = response.getEntity();
        String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");//返回结果
        EntityUtils.consume(entity);
        return jsonStr;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return "";
}
/** 加载客户端验证证书 **/
private static SSLContext getSSLContext(String keyStorePath, String trustStorePath) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, IOException, CertificateException, KeyManagementException {
    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    InputStream is = new FileInputStream(keyStorePath);
    keyStore.load(is, P12_PWD.toCharArray());
    is.close();
    keyManagerFactory.init(keyStore, P12_PWD.toCharArray());
    SSLContext ctx = SSLContext.getInstance("TLS");
    ctx.init(keyManagerFactory.getKeyManagers(), getTrustManagers(trustStorePath) , null);
    return ctx;
}
/** 加载信任证书 **/
private static TrustManager [] getTrustManagers (String ... crtPath) throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException {
    if (crtPath == null || crtPath.length < 1) {
        return null;
    }
    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    keyStore.load(null);
    for (int i=0, j=crtPath.length; i<j; i++) {
        String path = crtPath[i];
        InputStream is = new FileInputStream(path);
        keyStore.setCertificateEntry(Integer.toString(i), certificateFactory.generateCertificate(is));
        is.close();
    }
    TrustManagerFactory trustManagerFactory =TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init(keyStore);
    return trustManagerFactory.getTrustManagers();
}
```

在VM options中添加执行参数： `-Djavax.net.debug=all   -Djavax.net.ssl.trustStore=trustStore` 可以将双向认证的过程，在debug信息中打印出来
![](https://thumbnail0.baidupcs.com/thumbnail/557bfba3a7b82bdcc2b8b74029b89a8b?fid=2318483978-250528-1007737737813295&time=1497780000&rt=sh&sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-OmphjdusddoNYroutLQuFkZRTRA%3D&expires=8h&chkv=0&chkbd=0&chkpc=&dp-logid=3914448979859963184&dp-callid=0&size=c710_u400&quality=100&vuk=-&ft=video)

## 参考网站
[Nginx SSL 双向认证，key 生成和配置]( https://blog.imdst.com/nginx-ssl-shuang-xiang-ren-zheng-key-sheng-cheng-he-pei-zhi/)