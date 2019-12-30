/*
 * @lc app=leetcode id=9 lang=java
 *
 * [9] Palindrome Number
 */

// @lc code=start
class Solution {
    public boolean isPalindrome(int x) {
        if (x < 0 || (x > 0 && x % 10 == 0)) {
            return false;
        }
        int revert = 0;
        // 只需要反转一半
        while (revert < x) {
            revert = revert * 10 + x % 10;
            x /= 10;
        }
        // 奇数个数字，需要将 rever / 10
        return x == revert || x == revert / 10;
    }
}
// @lc code=end

