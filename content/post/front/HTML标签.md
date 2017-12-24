---
title: HTML 标签
---

# <video> 标签
`<video>`标签是HTML5的新标签，其属性如下：

属性     |  值      | 描述
:-------| :--------|:-------------------
autoplay| autoplay | 视频就绪后马上播放
controls| controls | 向用户展示控件，比如播放按钮等
height  | *pixels* | 视频播放器的高度
width   | *pixels* | 视频播放器的宽度
loop    | loop     | 当媒介文件完成播放后再次开始播放
muted   | muted    | 规定视频的音频输出应该被静音
poster  | *url*    | 规定视频下载时显示的图像，或者用户在点击播放按钮前显示的图像
preload | preload  | 视频在页面加载时进行加载，并预备播放
src     | *url*    | 要播放视频的url

## video 对象
video 对象是HTML5 中的新对象，video对象表示HTML `<video>` 元素。
1. 获取video元素: `var video = document.getElementById("videoId");`
2. 创建video对象: `var video = docuemnt.createElement("video");`

video 对象有如下属性：

属性          |  描述
:------------| :-------------
audioTracks  | 返回表示可用音频轨道的 AudioTrackList 对象。
autoplay     | 设置或返回是否在就绪（加载完成）后随即播放视频。
buffered     | 返回表示视频已缓冲部分的 TimeRanges 对象。
controller   | 返回表示视频当前媒体控制器的 MediaController 对象。
controls     | 设置或返回视频是否应该显示控件（比如播放/暂停等）。
crossOrigin  | 设置或返回视频的 CORS 设置。
currentSrc   | 返回当前视频的 URL。
currentTime  | 设置或返回视频中的当前播放位置（以秒计）。
defaultMuted |   设置或返回视频默认是否静音。
defaultPlaybackRate | 设置或返回视频的默认播放速度。
duration     | 返回视频的长度（以秒计）。
ended        | 返回视频的播放是否已结束。
error        | 返回表示视频错误状态的 MediaError 对象。
height       | 设置或返回视频的 height 属性的值。
loop         | 设置或返回视频是否应在结束时再次播放。
mediaGroup   | 设置或返回视频所属媒介组合的名称。
muted        | 设置或返回是否关闭声音。
networkState | 返回视频的当前网络状态。
paused       | 设置或返回视频是否暂停。
playbackRate | 设置或返回视频播放的速度。
played       | 返回表示视频已播放部分的 TimeRanges 对象。
poster       | 设置或返回视频的 poster 属性的值。
preload      | 设置或返回视频的 preload 属性的值。
readyState   | 返回视频当前的就绪状态。
seekable     | 返回表示视频可寻址部分的 TimeRanges 对象。
seeking      | 返回用户当前是否正在视频中进行查找。
src          | 设置或返回视频的 src 属性的值。
startDate    | 返回表示当前时间偏移的 Date 对象。
textTracks   | 返回表示可用文本轨道的 TextTrackList 对象。
videoTracks  | 返回表示可用视频轨道的 VideoTrackList 对象。
volume       | 设置或返回视频的音量。
width        | 设置或返回视频的 width 属性的值。

## video 坑
在手机端，各个浏览器厂商对 video 标签有不同的实现或商业调整。比如，QQ浏览器播放视频暂停时有广告，UC浏览器播放结束后有相关推荐。形成各种各样的坑。
对于QQ浏览器默认全屏的问题，可以指定video的宽高来解决。对于播放结束后有相关推荐的，可以在播放结束后删除video标签。