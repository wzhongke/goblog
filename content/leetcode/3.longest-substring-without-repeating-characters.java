import java.util.HashMap;
import java.util.Map;

/*
 * @lc app=leetcode id=3 lang=java
 *
 * [3] Longest Substring Without Repeating Characters
 */

// @lc code=start
class Solution {
    /**
     * 字符串 常用的窗口滑动算法
     */
    public int lengthOfLongestSubstring(String s) {
        if (s == null || s.length() == 0) {
			return 0;
		}
		int len = s.length();
		int subLen = 1;
        Map<Character, Integer> map = new HashMap<>();
        for(int i=0, j=0; j<len; j++) {
            if (map.containsKey(s.charAt(j))) {
                i = Math.max(map.get(s.charAt(j)), i);
            }
            subLen = Math.max(subLen, j - i + 1);
            map.put(s.charAt(j), j + 1);
        }
		return subLen;
    }
}
// @lc code=end

