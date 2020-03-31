/*
 * @lc app=leetcode.cn id=55 lang=java
 *
 * [55] 跳跃游戏
 */


/**
 * 从后往前找，可以跳到的设为 good
 */
// @lc code=start
class Solution {
    public boolean canJump(int[] nums) {
        boolean [] memo = new boolean[nums.length];
        for (int i = 0; i < memo.length; i++) {
            memo[i] = false;
        }
        memo[memo.length - 1] = true;

        for (int i = nums.length - 2; i >= 0; i--) {
            int furthestJump = Math.min(i + nums[i], nums.length - 1);
            for (int j = i + 1; j <= furthestJump; j++) {
                if (memo[j]) {
                    memo[i] = true;
                    break;
                }
            }
        }

        return memo[0];
    }
}
// @lc code=end

