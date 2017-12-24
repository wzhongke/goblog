---
title: sublime 配置
date: 2017-08-19 09:52:00
tags: ["工具"]
categories: ["工具"]
---

## 配置markdown
1. 安装package controll: 快捷键`ctrl+\``打开Sublime控制台，输入下面代码：
    sublime 3
    ```python
    import urllib.request,os,hashlib; h = 'df21e130d211cfc94d9b0905775a7c0f' + '1e3d39e33b79698005270310898eea76'; pf = 'Package Control.sublime-package'; ipp = sublime.installed_packages_path(); urllib.request.install_opener( urllib.request.build_opener( urllib.request.ProxyHandler()) ); by = urllib.request.urlopen( 'http://packagecontrol.io/' + pf.replace(' ', '%20')).read(); dh = hashlib.sha256(by).hexdigest(); print('Error validating download (got %s instead of %s), please try manual install' % (dh, h)) if dh != h else open(os.path.join( ipp, pf), 'wb' ).write(by)
    ```
    sublime 2
    ```
    import urllib2,os,hashlib; h = 'df21e130d211cfc94d9b0905775a7c0f' + '1e3d39e33b79698005270310898eea76'; pf = 'Package Control.sublime-package'; ipp = sublime.installed_packages_path(); os.makedirs( ipp ) if not os.path.exists(ipp) else None; urllib2.install_opener( urllib2.build_opener( urllib2.ProxyHandler()) ); by = urllib2.urlopen( 'http://packagecontrol.io/' + pf.replace(' ', '%20')).read(); dh = hashlib.sha256(by).hexdigest(); open( os.path.join( ipp, pf), 'wb' ).write(by) if dh == h else None; print('Error validating download (got %s instead of %s), please try manual install' % (dh, h) if dh != h else 'Please restart Sublime Text to finish installation')
    ```

2. 安装markdownediting: `ctrl+shift+p` 进入sublime命令面板，打开`install package`，输入`markdownediting`，安装后重启 sublime
3. 安装`OmniMarkupPreviewer`，同方法2
4. 配置markdownediting: 
    ```json
    {
        "color_scheme": "Packages/MarkdownEditing/MarkdownEditor-ArcDark.tmTheme",
        "draw_centered": false,
        "word_wrap": true,
        "wrap_width": "auto",
        "line_numbers": true
    }
    ```

## 安装格式化插件
1. 安装 JSFormat: `ctrl+shift+p` 进入sublime 命令面板，打开`install package`，输入`jsformat`，安装完成后重启sublime
2. 选中代码，使用快捷键`ctrl+alt+f`即可快速格式化代码

## 删除插件
1. `ctrl+shift+p`，打开命令面板，搜索`remove package`
2. 选中要删除的插件，确认即可

## 配置
```json
{
    "font_face": "Monaco",
    "font_size": 10,
    "ignored_packages":[],
    "line_padding_top": 3,

    // Additional spacing at the bottom of each line, in pixels
    "line_padding_bottom": 3,
    // translate tabs to four spaces
    "translate_tabs_to_spaces": false
}
```

## 仿照eclipse快捷键配置
```json
[
    { "keys": ["f12"], "command": "htmlprettify"},
    { "keys": ["f1"], "command": "fold" },
    { "keys": ["f2"], "command": "unfold" },
    { "keys": ["ctrl+l"], "command": "show_overlay", "args": {"overlay": "goto", "text": "@"} },

    { "keys": ["ctrl+space"], "command": "auto_complete" },
    { "keys": ["ctrl+space"], "command": "replace_completion_with_auto_complete", "context":
    [
    	{ "key": "last_command", "operator": "equal", "operand": "insert_best_completion" },
    	{ "key": "auto_complete_visible", "operator": "equal", "operand": false },
    	{ "key": "setting.tab_completion", "operator": "equal", "operand": true }
    ]
    },
    { "keys": ["ctrl+d"], "command": "run_macro_file", "args": {"file": "Packages/Default/Delete Line.sublime-macro"} },
    { "keys": ["ctrl+shift+c"], "command": "toggle_comment", "args": { "block": false } },
    { "keys": ["ctrl+shift+c"], "command": "toggle_comment", "args": { "block": true } },
    { "keys": ["ctrl+shift+f"], "command": "reindent" , "args": {"single_line": false}},
    { "keys": ["alt+up"], "command": "swap_line_up" },
    { "keys": ["alt+down"], "command": "swap_line_down" },
    { "keys": ["ctrl+alt+down"], "command": "duplicate_line" },
    { "keys": ["shift+ctrl+r"], "command": "show_overlay", "args": {"overlay": "goto", "show_files": true} },
    { "keys": ["ctrl+shift+s"], "command": "save_all" },
    { "keys": ["ctrl+l"], "command": "show_overlay", "args": {"overlay": "goto", "text": ":"} },
    { "keys": ["shift+ctrl+f4"], "command": "close_all" },
    { "keys": ["shift+ctrl+y"], "command": "lower_case" },
    { "keys": ["shift+ctrl+x"], "command": "upper_case" }
]
```