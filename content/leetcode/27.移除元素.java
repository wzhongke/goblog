/*
 * @lc app=leetcode.cn id=27 lang=java
 *
 * [27] 移除元素
 */

// @lc code=start
class Solution {
    public int removeElement(int[] nums, int val) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int end = nums.length-1;
        for (int i=0; i < end; ) {
            if (nums[i] == val) {
                nums[i] = nums[end];
                end--;
            } else {
                i ++;
            }
        }
        return end + 1;
    }
}
// @lc code=end

