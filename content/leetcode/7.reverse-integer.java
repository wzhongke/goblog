/*
 * @lc app=leetcode id=7 lang=java
 *
 * [7] Reverse Integer
 */

// @lc code=start
class Solution {
    /**
     * 使用数值计算
     * 需要判断溢出情况
     */
    public int reverse(int x) {
        int rev = 0;
        while (x != 0) {
            int pop = x % 10;
            x /= 10;
            if ( rev > Integer.MAX_VALUE / 10) {
                return 0;
            }
            if (rev < Integer.MIN_VALUE / 10) {
                return 0;
            }
            rev = rev * 10 + pop;
        }
        return rev;
    }
}
// @lc code=end

