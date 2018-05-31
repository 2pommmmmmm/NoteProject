笔记本
====
1.基础功能
---
	1.1显示时间戳
	
![](https://raw.githubusercontent.com/DerrickChanJL/DerrickChanJL.github.io/master/images/time.png)

<br>
修改数据库数据信息，添加一个修改时间的long字段
添加了一个TextView显示时间戳
	
	1.2搜索功能
![](https://raw.githubusercontent.com/DerrickChanJL/DerrickChanJL.github.io/master/images/search.jpg)
<br>
	
可根据title搜索
使用了toolbar + searchview + recyclerview 实现搜索功能
	
	
2.附加功能
----
	2.1美化UI

	2.2数据备份
	点击数据备份  数据库db文件会保存到手机内存中
	删除所有数据后可点击恢复数据 从保存到手机内存中的db文件恢复数据。
	
	使用SQLite对数据的增删改查，然后通过读写手机内存，把要备份的db文件写到手机内存中，恢复数据时再次把db文件读回到数据库中，从而实现数据备份和恢复
	
	2.3唤醒相机拍照并且保存到本地
![](https://raw.githubusercontent.com/DerrickChanJL/DerrickChanJL.github.io/master/images/n1.png)
![](https://raw.githubusercontent.com/DerrickChanJL/DerrickChanJL.github.io/master/images/n2.png)
![](https://raw.githubusercontent.com/DerrickChanJL/DerrickChanJL.github.io/master/archives/n3.png)
![](https://raw.githubusercontent.com/DerrickChanJL/DerrickChanJL.github.io/master/archives/n4.png)
	技术: 获取相机Intent，调取相机拍照保存到相机能识别的路径，再通过压缩图片的算法把照片显示出来
