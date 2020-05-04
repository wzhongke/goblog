/*
 * @lc app=leetcode.cn id=38 lang=java
 *
 * [38] 外观数列
 */

// @lc code=start
class Solution {
    public String countAndSay(int n) {
        if (n == 1) {
            return "1";
        }
        int num = 1;
        String s = "1";
        StringBuilder sb = new StringBuilder();
        while (num < n) {
            int count = 1;
            if (num != 1 ) s = sb.toString();
            sb = new StringBuilder();
            char first = s.charAt(0);
            for (int i=1; i<s.length(); i++) {
                if (s.charAt(i) == first) {
                    count ++;
                } else {
                    sb.append(count).append(first);
                    first = s.charAt(i);
                    count = 1;
                }
            }
            sb.append(count).append(first);
            num ++;
        }
        return sb.toString();
    }
}
// @lc code=end

