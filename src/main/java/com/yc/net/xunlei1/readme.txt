1. 多线程下载:
目标: 在一个http服务器上下载文件,多线程下载, 计算下载的文件字节数.

     步骤:    1. 在下载开始前要先获取到待下载文件的大小. 在本地创建一个空文件，占好空间.
                  a.先发一个请求 method: HEAD
                  b. Socket开发  Socket( 文件地址,端口)
                                 获取输出流，拼接协议 .
                    *** URL->URLConnection->HttpURLConnection
                           contentLength
                    以获取要下载的文件的大小.
                  c.   java.io.File:  createNewFile()  -> 这只是文件路径名的抽象.   xxx
                    解决方案，看2.
             2. 利用 java.io.  RandomAccessFile  类来指定文件大小，以创建新的空文件.   作用：在磁盘上占一个位置.
                      随机访问文件类： 读写,按指定的位置访问.   seek(  long position )

             3. 线程数的确定.
                 与cpu数保持.   System类取cpu数
             4. 根据文件大小和线程数计算每个线程下载的范围.

             5. 开始下载，创建线程，计算此线程的start,end .拼接协议:  Range: bytes=5001-10000
                    发出请求，下载指定部分.
                    HttpURLConnection.setRequestProperty(请求头域,值)
             6.  每个线程下载的量的累加问题:
               synchronized   ->    volatile( 有序性，可见性)  ->  long的原子性操作问题 -> java.util.concurrent.AtomicLong
                                                                                    -> int
