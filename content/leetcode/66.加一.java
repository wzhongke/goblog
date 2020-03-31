/*
 * @lc app=leetcode.cn id=66 lang=java
 *
 * [66] 加一
 */

// @lc code=start
class Solution {
    public int[] plusOne(int[] digits) {
        if (digits == null || digits.length == 0) {
            return new int[]{0};
        }
        int plus = 0;
        for (int i=digits.length - 1; i >= 0; i--) {
            int sum = digits[i];
            if (i==digits.length -1) {
                sum +=  1;
            } else {
                sum += plus;
            }
            
            if (sum < 10) {
                digits[i] = sum;
                return digits;
            } else {
                digits[i] = sum % 10;
                plus = 1;
            }
        }
        if (plus == 1) {
            int res [] = new int[digits.length + 1];
            res[0] = 1;
            for (int i=0; i<digits.length; i++) {
                res[i + 1] = digits[i];
            }
            return res;
        }
        return digits;
    }
}
// @lc code=end

