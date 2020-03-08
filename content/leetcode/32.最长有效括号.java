/*
 * @lc app=leetcode.cn id=32 lang=java
 *
 * [32] 最长有效括号
 */

// @lc code=start
class Solution {
    /**
     * 在这种方法中，我们利用两个计数器 leftleft 和 rightright 。首先，我们从左到右遍历字符串，对于遇到的每个 \text{‘(’}‘(’，我们增加 leftleft 计算器，对于遇到的每个 \text{‘)’}‘)’ ，我们增加 rightright 计数器。每当 leftleft 计数器与 rightright 计数器相等时，我们计算当前有效字符串的长度，并且记录目前为止找到的最长子字符串。如果 rightright 计数器比 leftleft 计数器大时，我们将 leftleft 和 rightright 计数器同时变回 00 。
     */
    public int longestValidParentheses(String s) {
        int left = 0, right = 0, maxlength = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                left++;
            } else {
                right++;
            }
            if (left == right) {
                maxlength = Math.max(maxlength, 2 * right);
            } else if (right >= left) {
                left = right = 0;
            }
        }
        left = right = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == '(') {
                left++;
            } else {
                right++;
            }
            if (left == right) {
                maxlength = Math.max(maxlength, 2 * left);
            } else if (left >= right) {
                left = right = 0;
            }
        }
        return maxlength;
    }

    /** 使用栈来做 */
    public int longestValidParenthesesStack(String s) {
        int maxans = 0;
        Stack<Integer> stack = new Stack<>();
        stack.push(-1);
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                stack.push(i);
            } else {
                stack.pop();
                if (stack.empty()) {
                    stack.push(i);
                } else {
                    maxans = Math.max(maxans, i - stack.peek());
                }
            }
        }
        return maxans;
    }
}
// @lc code=end

