---
title: mathjax 符号说明
date: 2017-12-27 12:50:00
tags: ["工具"]
categories: ["工具"]
---

## 使用MathJax插入公式
MathJax 插入的公式有两种形式：行内公式和独立公式。
1. 默认的行内公式是使用 `$...$`或 `\(...\)` 符号包裹内容。如 $a^2=b^2+c^2$
2. 默认的独立公式使用 `$$...$$` 或 `\[...\]` 包裹。如 
    $$a^2=b^2+c^2$$

## 希腊字母

希腊字母  | 符号      | 希腊字母    | 符号  
:--------|:----------|:----------|:--------
$\alpha$ | `\alpha`  | $\varpi$   | `\varpi`
$\beta$  | `\beta`   | $\rho$     | `\rho`
$\gamma$ | `\gamma`  | $\varrho$  | `\varrho`
$\Gamma$ | `\Gamma`  | $\sigma$   | `\sigma`
$\delta$ | `\delta`  | $\Sigma$   | `\Sigma`
$\Delta$ | `\delta`  | $\varsigma$| `\varsigma`
$\epsilon$ | `\epsilon` | $\tau$  | `\tau`
$\varepsilon$ | `\varepsilon` | $\upsilon$ | `\upsilon`
$\zeta$  | `\zeta`   | $\phi$ | `\phi`
$\eta$   | `\eta`    | $\Phi$ | `\Phi`
$\theta$ | `\theta`  | $\varphi$ | `\varphi`
$\vartheta$ | `\vartheta` | $\chi$ | `\chi`
$\iota$  | `iota`    | $\psi$ | `\psi`
$\kappa$ | `\kappa`  | $\Psi$ | `\Psi`
$\lambda$ | `\lambda` | $\omega$ | `\omega`
$\Lambda$ | `\Lambda` | $\Omega$ | `\Omega`
$\mu$     | `\mu` |$\Xi$     | `\Xi`
$\nu$     | `\nu` | $\pi$     | `\pi`
$\xi$     | `\xi` | $\Pi$     | `\Pi`

## 数学符号

数学符号  | 符号      |  数学符号    | 符号
:--------|:----------|:------------|:--------
$\pm$    | `\pm`     | $\emptyset$ | `\emptyset`
$\times$ | `\times`  | $\in$       | `\in`
$\div$   | `\div`    | $\notin$    | `\notin`
$\mid$   | `\mid`    | $\subset$   | `\subset`
$\nmid$  | `\nmid`   | $\supset$   | `\supset`
$\cdot$  | `\cdot`   | $\subseteq$ | `\subseteq`
$\bigodot$| `\bigodot`| $\supseteq$ | `\supseteq`
$\bigotimes$|`\bigotimes`| $\bigcap$ | `\bigcap`
$\bigoplus$| `\bigoplus`| $\bigcup$ | `\bigcup`
$\leq$   | `\leq`    | $\bigvee$     | `\bigvee`
$\geq$   | `\geq`    | $\bigwedge$   | `\bigwedge`
$\neq$   | `\neq`    | $\biguplus$ | `\biguplus`
$\approx$| `\approx` | $\bigsqcup$ | `\bigsqcup`
$\equiv$ | `\equiv`  | $\nabla$  | `\nabla`
$\sum$   | `\sum`    | $\log$    | `\log`
$\prod$  | `\prod`   | $'$       | `'`
$\coprod$| `\coprod` | $\angle$  | `\angle`
$\int$   | `\int`    | $\iint$   | `\iint`
$\iiint$ | `\iiint`  | $\oint$   | `\oint`
$\lim$   | `\lim`    | $\infty$  | `\infty`

## 数学逻辑运算符

数学符号    |   符号     | 数学符号 | 符号
:----------|:-----------|:--------|:---------
$\because$ | `\because` | $\therefore$ | `\therefore`
$\forall$  | `\forall`  | $\exists$ | `\exists`
$\not\subset$ | `\not\subset`

## 箭头符号

箭头符号    | 符号         | 箭头符号    | 符号
:-----------|:------------|:-----------|:------------
$\uparrow$  | `\uparrow`  | $\Uparrow$  | `\Uparrow` 
$\downarrow$ | `\downarrow`| $\Downarrow$ | `\Downarrow`
$\updownarrow$ | `\updownarrow` | $\Updownarrow$ | `\Updownarrow`
$\rightarrow$ | `\rightarrow` | $\Rightarrow$   | `\Rightarrow`
$\leftarrow$ | `\leftarrow`  | $\Leftarrow$   | `\Leftarrow`
$\leftrightarrow$ | `\leftrightarrow` | $\Leftrightarrow$ | `\Leftrightarrow`
$\longrightarrow$ | `\longrightarrow` | $\Longrightarrow$ | `\Longrightarrow`
$\longleftarrow$ | `\longleftarrow` | $\Longleftarrow$  | `\Longleftarrow`
$\longleftrightarrow$ | `\longleftrightarrow` | $\Longleftrightarrow$ | `\Longleftrightarrow`
$\rightleftharpoons$ | `\rightleftharpoons`  | $\leadsto$  | `\leadsto`
$\nleftarrow$  | `\nleftarrow`   | $\nrightarrow$ | `\nleftarrow`
$\nearrow$ | `\nearrow` |  $\searrow$  | `\searrow`
$\swarrow$ | `\swarrow` | $\nwarrow$   | `\nwarrow`

## 运算
{{<html>}}
$$ e[i,j]=\left\{ \begin{aligned}
q_{i-1} & = & if j=i-1 \\
\min_{i\le r \le j}((e[i,r-1]+e[r+1,j]+\omega(i,j))) & = & if i\le j 
\end{aligned}
\right.
$$

$$e[i,j]=\left\{ \begin{array}{ll}
q_{i-1}& \textrm{if $j=i-1$}\\
\min_{i\le r\le j}(e[i,r-1]+e[r+1,j]+\omega(i,j)) & \textrm{if $i\le j$}
\end{array} \right.
$$
{{<\html>}}


## 其他
原始符号不会随着公式大小而缩放，可以使用 `\left( ... \right)` 来代替 `()`:
$$\{\sum_{i=0}^n\}$$

$$\left\{\sum_{i=0}^n\right\}$$