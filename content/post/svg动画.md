---
title: svg动画
date: 2017-07-02 19:42:25
tags: ["svg"]
categories: ["svg"]
---

svg 字体动画
<!-- more -->
{% raw %}
<style type="text/css">
.center-btn-wrapper {
    width: 100%;
    top: 50%;
    text-align: center;
    font-size: 24px;
}
.center-btn-wrapper a {
    color: white;
    text-decoration: none;
    text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.42);
    background-color: #D87B00;
    padding: 12px 30px;
    border-radius: 5px;
}
.center-btn-wrapper a:hover {
    background-color: #14516F;
}
/* 手机适配 */
@media screen and (max-width: 768px) {
	.center-btn-wrapper {
	    font-size: 18px;
	}
}
/* 暂时隐藏CNZZ统计按钮 */
span.cnzz-wrapper {
    display: none;
}
#svg {
	display: block;
	margin: 0 auto;
}
#svg path {
	/*虚线长度足够长，至少要大于整个path的长度*/
	stroke-dasharray: 3498;
	-webkit-animation: dash 15s linear infinite alternate;
	animation: dash 15s linear infinite alternate;
}
@-webkit-keyframes dash {
	0%{stroke-dashoffset: 3498;stroke-dasharray: 3498;stroke:red;}
	50%{stroke-dashoffset: 0;stroke-dasharray: 20;stroke:#D302D6;}
	100%{stroke-dashoffset: 1000;stroke-dasharray: 20;stroke:green;}
}
@keyframes dash {
	0%{stroke-dashoffset: 3498;stroke-dasharray: 3498;stroke:red;}
	50%{stroke-dashoffset: 0;stroke-dasharray: 20;stroke:#D302D6;}
	100%{stroke-dashoffset: 1000;stroke-dasharray: 20;stroke:green;}
}
</style>

<div class="center-btn-wrapper">
	<svg id="svg" xmlns="http://www.w3.org/2000/svg" height="404" width="452">
		 <g>
		  <title>Layer 1</title>
		  <path fill-opacity="null" stroke-opacity="null" stroke-width="3" stroke="#41ABF7" fill="none" d="m31.919427,29.815911c0,0 323.1068,0.991126 323.1068,0.991126c0,0 31.716006,-27.751503 31.716006,-27.751503c0,0 43.609516,49.556251 43.609516,49.556251c0,0 -185.34041,0 -185.34041,0c0,0 0,141.730913 0,141.730913c0,0 96.139145,0 95.643186,-0.00002c0.495959,0.00002 25.274085,-24.778126 25.274085,-24.778126c0,0 39.645013,46.582894 39.645013,46.582894c0,0 -160.562284,0.991126 -160.562284,0.991126c0,0 0,154.615509 0,154.615509c0,0 139.748642,0 139.748642,0c0,0 26.760377,-26.760377 26.760377,-26.760377c0,0 42.618391,48.565145 42.618391,48.565145c0,0 -401.405681,-1.982252 -401.405681,-1.982252c0,0 -14.866887,2.973377 -14.866887,2.973377c0,0 -14.866868,2.973377 -14.866868,2.973377c0,0 -21.804748,-25.769271 -21.804748,-25.769271c0,0 204.171761,-0.991126 204.171761,-0.991126c0,0 0,-154.615509 0,-154.615509c0,0 -84.245635,-0.991126 -84.245635,-0.991126c0,0 -15.857993,0 -15.857993,0c0,0 -11.89351,1.982252 -11.89351,1.982252c0,0 -13.875742,1.982252 -13.875742,1.982252c0,0 -21.804748,-22.795894 -21.804748,-22.795894c0,0 147.677629,-0.991106 147.677629,-0.991106c0,0 0,-143.713145 0,-143.713145c0,0 -118.935,-0.991126 -118.935,-0.991126c0,0 -29.733755,3.964483 -29.733755,3.964483c0,0 -6.937881,2.973377 -6.937881,2.973377c0,0 -17.840265,-27.751503 -17.840265,-27.751503z" id="svg_8"/>
		 </g>
	</svg>
</div>
{% endraw %}

css源代码：
```css
.center-btn-wrapper {
    width: 100%;
    top: 50%;
    text-align: center;
    font-size: 24px;
}
.center-btn-wrapper a {
    color: white;
    text-decoration: none;
    text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.42);
    background-color: #D87B00;
    padding: 12px 30px;
    border-radius: 5px;
}
.center-btn-wrapper a:hover {
    background-color: #14516F;
}
/* 手机适配 */
@media screen and (max-width: 768px) {
    .center-btn-wrapper {
        font-size: 18px;
    }
}
/* 暂时隐藏CNZZ统计按钮 */
span.cnzz-wrapper {
    display: none;
}
#svg {
    display: block;
    margin: 0 auto;
}
#svg path {
    /*虚线长度足够长，至少要大于整个path的长度*/
    stroke-dasharray: 3498;
    -webkit-animation: dash 15s linear infinite alternate;
    animation: dash 15s linear infinite alternate;
}
@-webkit-keyframes dash {
    0%{stroke-dashoffset: 3498;stroke-dasharray: 3498;stroke:red;}
    50%{stroke-dashoffset: 0;stroke-dasharray: 20;stroke:#D302D6;}
    100%{stroke-dashoffset: 1000;stroke-dasharray: 20;stroke:green;}
}
@keyframes dash {
    0%{stroke-dashoffset: 3498;stroke-dasharray: 3498;stroke:red;}
    50%{stroke-dashoffset: 0;stroke-dasharray: 20;stroke:#D302D6;}
    100%{stroke-dashoffset: 1000;stroke-dasharray: 20;stroke:green;}
}
```
html源码
```html
<div class="center-btn-wrapper">
    <svg id="svg" xmlns="http://www.w3.org/2000/svg" height="404" width="452">
         <g>
          <title>Layer 1</title>
          <!-- path 是用svg工具生成的 -->
          <path fill-opacity="null" stroke-opacity="null" stroke-width="3" stroke="#41ABF7" fill="none" d="m31.919427,29.815911c0,0 323.1068,0.991126 323.1068,0.991126c0,0 31.716006,-27.751503 31.716006,-27.751503c0,0 43.609516,49.556251 43.609516,49.556251c0,0 -185.34041,0 -185.34041,0c0,0 0,141.730913 0,141.730913c0,0 96.139145,0 95.643186,-0.00002c0.495959,0.00002 25.274085,-24.778126 25.274085,-24.778126c0,0 39.645013,46.582894 39.645013,46.582894c0,0 -160.562284,0.991126 -160.562284,0.991126c0,0 0,154.615509 0,154.615509c0,0 139.748642,0 139.748642,0c0,0 26.760377,-26.760377 26.760377,-26.760377c0,0 42.618391,48.565145 42.618391,48.565145c0,0 -401.405681,-1.982252 -401.405681,-1.982252c0,0 -14.866887,2.973377 -14.866887,2.973377c0,0 -14.866868,2.973377 -14.866868,2.973377c0,0 -21.804748,-25.769271 -21.804748,-25.769271c0,0 204.171761,-0.991126 204.171761,-0.991126c0,0 0,-154.615509 0,-154.615509c0,0 -84.245635,-0.991126 -84.245635,-0.991126c0,0 -15.857993,0 -15.857993,0c0,0 -11.89351,1.982252 -11.89351,1.982252c0,0 -13.875742,1.982252 -13.875742,1.982252c0,0 -21.804748,-22.795894 -21.804748,-22.795894c0,0 147.677629,-0.991106 147.677629,-0.991106c0,0 0,-143.713145 0,-143.713145c0,0 -118.935,-0.991126 -118.935,-0.991126c0,0 -29.733755,3.964483 -29.733755,3.964483c0,0 -6.937881,2.973377 -6.937881,2.973377c0,0 -17.840265,-27.751503 -17.840265,-27.751503z" id="svg_8"/>
         </g>
    </svg>
</div>
```
