import java.util.Arrays;

/*
 * @lc app=leetcode.cn id=16 lang=java
 *
 * [16] 最接近的三数之和
 */

// @lc code=start
class Solution {
    public int threeSumClosest(int[] nums, int target) {
        if (nums == null || nums.length < 3) {
            return -1;
        }
        int len = nums.length;
        Arrays.sort(nums);
        int result = nums[0] + nums[1] + nums[2];
        for (int i = 0; i < len - 2; i++) {
            for (int first = i + 1, last = len - 1; first < last; ) {
                int sum = nums[i] + nums[first] + nums[last];
                if(Math.abs(target - sum) < Math.abs(target - result)) {
                    result = sum;
                }
                if (sum > target) {
                    last--;
                } else if (sum< target){
                    first++;
                } else {
                    return sum;
                }

            }
        }
        return result;
    }
}
// @lc code=end

