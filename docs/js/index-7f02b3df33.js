"use strict";$(function(){initPlugins(),setTocToggle(),setAsideToggle(),loadSvg(),setSearchBoard()});var initPlugins=function(){$(".tab").click(function(e){window.location.href=$(this).find("a").prop("href")}),$(".modal").modal()},loadSvg=function(){var e=window.origin+"/svg/icon.svg";$('<div style="display:none"></div>').appendTo($("body")).load(e)},loadLunrDB=function(){var e=null;return function(){if(e)return e;var t=window.origin+"/index.json";return e=$.getJSON(t).then(function(e){return{pageMap:e.reduce(function(e,t){return e[t.uri]=t,e},Object.create(null)),index:lunr(function(){var t=this;this.field("title",{boost:10}),this.ref("uri"),e.forEach(function(e){return t.add(e)})})}}).fail(function(t,n,o){console.error("Error getting Hugo index flie:",n+", "+o),e=null})}}(),search=function(e){return loadLunrDB().then(function(t){return t.index.search(e).map(function(e){return t.pageMap[e.ref]})})},setSearchBoard=function(){var e=$("#in-search"),t=$("#out-search"),n=function(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:"No Result...",n=$('<span class="collection-item text-grey" style="display:none">'+e+"</span>");t.empty().append(n.fadeIn(200))};n();e.keyup(function(){var o=e.val().trim();o.length<1?n():(n("Searching..."),search(o).then(function(e){return function(e){if(e&&e.length){$.isArray(e)||(e=[e]);var o=e.map(function(e){return $('<a href="'+e.uri+'" class="collection-item nowrap" style="display:none"><i class="material-icons">description</i>'+e.title+"</a>")});t.empty().append(o),o.forEach(function(e){return e.fadeIn(100)})}else n()}(e)}).fail(function(){return n("Fail to execute search, Please check your network.")}))})},setTocToggle=function(){function e(){return a.map(function(e){return Math.floor($(e.getAttribute("href")).offset().top-c)})}var t=!0,n=$(".toc-panel nav"),o=($("footer.page-footer"),$(".post .card")),i=$("nav.navbar"),a=[].slice.call(n.find("li a")),r=n.outerHeight(),s=o.offset(),l=i.height(),c=l+20,u=e();if(0!==n.length){n.on("click","a",function(e){var o=this;e.preventDefault(),e.stopPropagation(),t=!1,n.find("a").removeClass("active"),function(e,t){var n=e.href?$(e.getAttribute("href")):$(e);$("html, body").animate({scrollTop:n.offset().top-c+1},400,t)}(this,function(){t=!0,$(o).addClass("active")})});var f=function(){if(u){var e=o.height(),i=$("html").scrollTop()||$("body").scrollTop(),c=!1;if(i+l>=s.top&&(n.removeClass("absolute").addClass("fixed").css("top",l),c=!0),i+l+r>=s.top+e&&(n.removeClass("fixed").addClass("absolute").css({top:e-r}),c=!0),c||n.removeClass("fixed").removeClass("absolute").css({top:"initial"}),t){for(var f,d=0,p=u.length-1;d<p;)u[f=d+p+1>>1]===i?d=p=f:u[f]<i?d=f:p=f-1;$(a).removeClass("active").eq(d).addClass("active")}}};$(window).resize(function(){u=e(),s=o.offset(),r=n.outerHeight(),l=i.height(),c=l+20,t=!0,f()}),$(window).scroll(function(){return f()}),f()}},setAsideToggle=function(){var e=$("aside.side-panel"),t=$("body"),n=$(".button-collapse"),o=$("i.material-icons",n),i=$('<div id="js-cover"></div>').appendTo(t),a=function(){e.hasClass("open")?(e.removeClass("open"),i.fadeOut(400),t.removeClass("covered"),o.text("menu")):(e.addClass("open"),i.fadeIn(400),t.addClass("covered"),o.text("close"))};i.click(function(e){e.stopPropagation(),e.preventDefault(),a()}),n.click(a)};