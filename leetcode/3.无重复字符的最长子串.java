/*
 * @lc app=leetcode.cn id=3 lang=java
 *
 * [3] 无重复字符的最长子串
 */

// @lc code=start
class Solution {
    public int lengthOfLongestSubstring(String s) {
        if (s == null || s.length() == 0) {
			return 0;
		}
		int len = s.length();
		int subLen = 1;
		Map<Character, Integer> map = new HashMap<>();
		int i=0, j=0;
		while (i<len && j<len) {
			char c = s.charAt(j);
			if (map.containsKey(c)) {
				subLen = Math.max(subLen, j - i);
				i =  Math.max(i, map.get(c) + 1);
				map.remove(c);
			} 
            map.put(c, j++);
		}
		return  Math.max(subLen, j - i);
    }
}
// @lc code=end

