# VIP资源解析


### 酷狗音乐下载地址解析
1. 拿到歌曲的hash值
2. 根据hash值算出md5值
3. 将hash值和md5值发送给后端接口拿到下载地址

### 腾讯视频真实地址解析
1. 根据视频地址通过正则拿到视频的vid
2. 构造参数拿到视频的url_prefix、streamID、filename值
3. 根据第二步拿到的值再次构造参数请求接口拿到视频key值
4. 根据视频的url_prefix、filename、key值拼接出视频的真实地址
