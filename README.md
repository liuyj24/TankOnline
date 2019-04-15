# TankOnline
- 网络对战坦克小游戏
- 写了篇项目介绍的博客, [点击跳转到博客地址](https://www.cnblogs.com/tanshaoshenghao/p/10708586.html)

## 单机测试
- 导入项目后, 先运行`TankServer`. 
- 然后运行`TankClient`
- 在`My UDP Port`中输入一个4位数的端口号(可用默认的`5555`), 点击`connect to server`启动一个客户端
- 继续新运行一个`TankClient`, 并在`My UDP Port`输入一个不同的UDP端口号, 比如`5556`
- 这样就开启了两个客户端, 可以做对战测试了, 当然还可以开更多个

## 联机测试
- 一台主机运行`TankServer`, 关闭防火墙, 查看自己的ip地址
- 在不同的的主机上运行`TankClient`客户端, 在`IP:`栏输入服务器主机的ip地址, 并在`My UDP Port`选择4位端口号进行连接, `Port`是服务器TCP端口使用默认值`8888`
- 由于在不同的主机上运行, `My UDP Port`在不同主机上是可以一样的.

## 游戏中的操作
- W A S D 对应上下左右移动, J 键开火. 
- 死亡后可重新启动`TankClient`进入游戏. 
- 若在宿舍等小范围场所内对战要注意安全
- 提示: 死亡后重新运行仍可连上服务器进行复仇. 
