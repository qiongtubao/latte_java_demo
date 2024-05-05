# -*- coding: utf-8 -*-
import socket
import random
import string

def generate_random_string():
    length = random.randint(1024, 1024)
    return ''.join(random.choice(string.ascii_letters) for _ in range(length))

def echo_client(host, port):
    # 创建一个TCP套接字
    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        # 连接服务器
        client_socket.connect((host, port))
        
        while True:
            # 发送消息到服务器
            message = generate_random_string()
            client_socket.sendall(message)

            # 接收服务器返回的消息
            data = client_socket.recv(1024)

            # 打印接收到的消息
            print "Received:", data

    finally:
        # 关闭套接字连接
        client_socket.close()

if __name__ == '__main__':
    host = 'localhost'  # 服务器主机名
    port = 8080  # 服务器端口号
    
    echo_client(host, port)
