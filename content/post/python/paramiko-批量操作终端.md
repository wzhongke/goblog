---
title: paramiko-批量操作终端
date: 2017-12-06 19:42:25
tags: ["python"]
categories: ["python"]
---

# paramiko
Paramiko 是SSHv2协议的 python(2.7, 3.4+) 实现，提供了client和server的功能。使用该模块可以方便的进行ssh连接或者sftp协议的传输。

# argparse
argparse 模块可以使解析命令行参数变得非常简单。在程序中定义其所需的参数，argparse 将会将命令行的参数解析并生成对象，命令行中的各个参数被保存对象的属性。以下代码中简单描述了 argparse 的使用方式：

```python
import argparse

'''低版本的Python不支持 argparse'''

'''首先创建一个 `argparse.ArugmentParser` 对象 '''
parser = argparse.ArgumentParser(description='Process some integers.')
'''使用`add_argument()`方法来添加将命令行参数解析成对象的方式，这些对象在`parse_args()`方法调用后才可以使用。'''
parser.add_argument('integers', metavar='N', type=int, nargs='+',
                    help='an integer for the accumulator')
'''通过命令行参数可以指定`accumulate`属性是`sum`函数，还是`max`函数'''
parser.add_argument('-s', '--sum', dest='accumulate', action='store_const',
                    const=sum, default=max,
                    help='sum the integers (default: find the max)')
parser.add_argument("--foo", action='append')

'''
上述代码可以通过如下命令行调用
`python prog.py 1 2 3 4`   结果： 4
·ptython prog.py 1 2 3 4 --sum` 结果： 10
'''

'''
add_argument() 方法的参数定义如下：
- name or flags: 一个名称或一个选项字符串列表
- action: 命令行中遇到该参数时，采用基本类型的操作
   1. `store`: 仅仅存储参数的值
   2. `store_const`: 存储由`const`指定的参数，该方式通常用来存储指定某种操作的可选参数
   3. `store_true`/`store_false`: `store_const`的特例，将值存储为True/False
   4. `append`: 将各个参数的值存储为一个列表，允许多个可选参数时非常有用 
   5. `append_const`: 存储由`const`指定的参数到list中。
- nargs: 应该使用的命令行参数的数量
- const: 被action和nargs所需的常量值
- default: 如果命令行中不存在该参数，所采取的默认操作
- type: 命令行参数应该被转换成的类型
- choices: 可以被允许的值
- required: 是否可以省略的命令行选项
- help: 简要介绍参数的用途
- metavar: 用法消息参数的名称
- dest: `parse_args()` 返回的对象中属性的名字，对应于该参数。
'''


class FooAction(argparse.Action):
    def __init__(self, option_strings, dest, nargs=None, **kwargs):
        if nargs is not None:
            raise ValueError("nargs not allowed")
        super(FooAction, self).__init__(option_strings, dest, **kwargs)

    def __call__(self, parser, namespace, values, option_string=None):
        print('%r %r %r' % (namespace, values, option_string))
        setattr(namespace, self.dest, values)


'''
nargs: 应该使用的命令行参数的数量
- N: `N` 个命令行参数将会被放到一个list中。
- ?: 如果可能的话，从命令行中读取一个参数，产生一个 
- *: 所有的命令行参数都会被放到同一个list中
- +: 同`*`类似，只是要求至少有一个参数，否则会抛出异常
'''


parser.add_argument('--foo', nargs='*')
parser.add_argument('--bar', nargs='*')
parser.add_argument('baz', nargs='*')
'''parser.parse_args('a b --foo x y --bar 1 2'.split()):Namespace(bar=['1', '2'], baz=['a', 'b'], foo=['x', 'y']) '''

'''将命令行参数转换成合适的对象'''
args = parser.parse_args()
print(args.accumulate(args.integers))
```

# 使用 paramiko 批量处理终端
在使用 paramiko 批量处理终端时，如果使用串行的处理方式，每个机器都等上个机器处理完之后再进行操作，那么可以预见将要耗费较长的时间。因此，我们采用了多线程的处理方式。但是，一个机器的资源是有限的，如果批量处理机器太多，每个机器开一个线程的方式显然是浪费资源且低效的，可以使用线程池的方式处理。

```python
import argparse
import paramiko
from concurrent.futures import ThreadPoolExecutor


def parse_argument():
    """解析参数"""
    parser = argparse.ArgumentParser(description='Process some integers.')
    parser.add_argument("-f", "--file", type=str, dest="file", default="machine", help="the file of ip address")
    parser.add_argument("-e", "--command", metavar='N', nargs='+', type=str, dest="command",
                        help="the command to execute")
    parser.add_argument("-g", "--grep", type=str, dest="grep", metavar='N', nargs='+', default=None,
                        help="grep some thing")
    args = parser.parse_args()
    if args.command is not None:
        args.command = " ".join(args.command)
    if args.grep is not None:
        args.command = "grep --color=always -P " + " ".join(args.grep)
    return args


def execute(host, username, password, command):
    """连接终端执行命令"""
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    ssh.connect(host, port=22, username=username, password=password, timeout=7)
    stdin, stdout, stderr = ssh.exec_command(command)
    cmd_result = stdout.readlines(encoding="gbk")
    err_result = stderr.readlines()
    result = "----------------------" + host + "--------------------------------\n"
    for o in cmd_result:
        print(o)
    return result + "------------------------\n"

if __name__ == '__main__':
    args = parse_argument()
    hosts = []

    print(args)
    if args.file is not None:
        with open(args.file, encoding="utf8") as hostFile:
            for line in hostFile:
                hosts.append(line.strip())
    pool = ThreadPoolExecutor(max_workers=2)
    futures = []
    print(hosts)
    for host in hosts:
        futures.append(pool.submit(execute, host, "guest", "Sogou@)!$", args.command))
    for future in futures:
        print(future.result())
```

上述代码中有两点需要特别说明一下：
1. 对于使用`grep`命令时，加入`--color=always`。这是因为`always`会在任何情况下都给匹配字段加上颜色标记，这也会导致在某些终端下，出现字符颜色标记乱码的情况
2. paramiko 并不支持 `stdout.readlines(encoding="gbk")` 这种调用方式。因为其默认会采用utf8编码，而有些机器使用的是gbk方式，这会导致不能正确解析文本，出现乱码。因此，我对 paramiko 中的解码部分进行了如下调整：
    修改file.py文件中的`def readlines` 函数和 `def readline` 函数为：

    ```python
    def readlines(self, sizehint=None, encoding="utf-8"):
        lines = []
        byte_count = 0
        while True:
            // 给 readline 透传了 encoding 参数
            line = self.readline(encoding=encoding)
            if len(line) == 0:
                break
            lines.append(line)
            byte_count += len(line)
            if (sizehint is not None) and (byte_count >= sizehint):
                break
        return lines
    ...
    def readline(self, size=None, encoding="utf-8"):
        ...
        return line if self._flags & self.FLAG_BINARY else u(line, encoding=encoding)
    ```
    
    因为 `readline` 函数最后的`return` 语句中含有 `encoding` 参数，只要把编码参数透传给它即可。
3. 执行命令时，可能会遇到二进制文件，导致抛出编码异常`UnicodeDecodeError: 'gbk' codec can't decode byte 0xfd in position 246: illegal multibyte sequence`。查看`decode`的API，发现有一个`errors`参数。该参数可以在遇到错误时，选择如何处理错误。我的解决方案是修改 py3compat.py 文件中的 `def u(s, encoding='utf8')` 函数为：
    
    ```python
    if isinstance(s, bytes):
            return s.decode(encoding, errors="ignore")
        elif isinstance(s, str):
            return s
        else:
            raise TypeError("Expected unicode or bytes, got {!r}".format(s))
    ```
