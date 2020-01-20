/*
 * @lc app=leetcode.cn id=11 lang=java
 *
 * [11] 盛最多水的容器
 */

// @lc code=start
class Solution {
    public int maxArea(int[] height) {
        if (height == null || height.length == 0) {
			return 0;
		}
		int i = 0;
		int j = height.length - 1;
		int max = 0;
		while ( i < j) {
			if (height[i] > height[j]) {
                max = Math.max(max, (j - i) * height[j]);
				j--;
			} else {
                max = Math.max(max, (j - i) * height[i]);
				i++;
			}
		}
		return max;
    }
}
// @lc code=end

