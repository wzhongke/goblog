import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;


/*
 * @lc app=leetcode.cn id=18 lang=java
 *
 * [18] 四数之和
 */

// @lc code=start
class Solution {
    public List<List<Integer>> fourSum(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length < 3) {
            return result;
        }
        int len = nums.length;
        Arrays.sort(nums);
        
        for(int i=0; i<len-3;) {
            for (int j=i+1; j<len-2;) {
                for (int first = j + 1, last = len - 1; first < last;) {
                    int sum = nums[i] + nums[j] + nums[first] + nums[last];
                    if (sum == target) {
                        result.add(new ArrayList<>(Arrays.asList(nums[i], nums[j], nums[first], nums[last])));
                    }
                    if (sum < target) {
                        while (first < last && nums[first] == nums[++first]); // 如果相等就跳过
                    } else {
                        while (first < last && nums[last] == nums[--last]); // 如果相等就跳过
                    }
                }
                while (j+1<len && nums[j] == nums[++j]);
            }
            while ( i+2<len && nums[i] == nums[++i]);
        }
        return result;
    }
}
// @lc code=end

