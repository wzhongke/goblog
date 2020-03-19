/*
 * @lc app=leetcode.cn id=41 lang=java
 *
 * [41] 缺失的第一个正数
 */

// @lc code=start
class Solution {
    public int firstMissingPositive(int[] nums) {
        if (nums == null || nums.length == 0 ) {
            return 1;
        }
        int len = nums.length;
        for (int i=0; i<nums.length; i++) {
            while (nums[i] > 0 && nums[i] < len && 
                nums[i] != i + 1 && nums[i] != nums[nums[i] - 1]) {
                int tmp = nums[i];
                nums[i] = nums[tmp - 1];
                nums[tmp - 1] = tmp;
            }
        }
        for (int i=0; i<len; i++) {
            if (nums[i] != i + 1) {
                return i + 1;
            }
        }
        return len + 1;
    }
}
// @lc code=end

