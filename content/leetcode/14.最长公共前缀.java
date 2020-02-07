/*
 * @lc app=leetcode.cn id=14 lang=java
 *
 * [14] 最长公共前缀
 */

// @lc code=start
class Solution {
    public String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) {
			return "";
		}
		StringBuilder prefix = new StringBuilder();
		int minLen = Integer.MAX_VALUE;
		for (String str: strs) {
			minLen = Math.min(str.length(), minLen);
		}
	
		for (int i=0; i<minLen; i++) {
			char c = strs[0].charAt(i);
			for (int j=1; j<strs.length; j++) {
				if (c != strs[j].charAt(i)) {
					return prefix.toString();
				}
			}
			prefix.append(c);
		}
		return prefix.toString();
    }
}
// @lc code=end

