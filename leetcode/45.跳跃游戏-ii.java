/*
 * @lc app=leetcode.cn id=45 lang=java
 *
 * [45] 跳跃游戏 II
 */

// @lc code=start
class Solution {
    public int jump(int[] nums) {
        int len = nums.length;
        int [] count = new int[len];
        for (int i=0; i<nums.length; i++) {
            for (int j=i+1, cnt=0; j<len && cnt<nums[i]; j++, cnt++) {
                if (count[j] == 0) {
                    count[j] = count[i] + 1;
                } else {
                    count[j] = Math.min(count[i] + 1, count[j]);
                }
            }
        }
        return count[len - 1];
    }
}
// @lc code=end

