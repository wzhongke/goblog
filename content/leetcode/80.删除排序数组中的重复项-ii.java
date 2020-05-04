/*
 * @lc app=leetcode.cn id=80 lang=java
 *
 * [80] 删除排序数组中的重复项 II
 */

// @lc code=start
class Solution {
    public int removeDuplicates(int[] nums) {
        if (nums == null || nums.length <= 2) {
            return nums.length;
        }
        int i= 1;
        int j = i + 1;
        while (j<nums.length) {
            while (j<nums.length && nums[j] == nums[i-1]) {
                j++;
            }
            if (i + 1 < nums.length && j<nums.length) {
                i++;
                nums[i] = nums[j];
                j++;
            }
        }
        return i + 1;
    }
}
// @lc code=end

