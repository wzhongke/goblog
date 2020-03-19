import java.util.Arrays;
import java.util.Deque;

/*
 * @lc app=leetcode.cn id=47 lang=java
 * 链接：https://leetcode-cn.com/problems/permutations-ii/solution/hui-su-suan-fa-python-dai-ma-java-dai-ma-by-liwe-2/
 * [47] 全排列 II
 */

// @lc code=start
class Solution {
    public List<List<Integer>> permuteUnique(int[] nums) {
        Arrays.sort(nums);
        int len = nums.length;
        // 使用 Deque 是 Java 官方 Stack 类的建议
        Deque<Integer> path = new ArrayDeque<>(len);
        List<List<Integer>> res = new ArrayList<>();
        boolean [] used = new boolean[len];
        backtrack(len, nums, path, res, 0, used);
        return res;
    }

    private void backtrack(int len, int[] nums, Deque<Integer> path, List<List<Integer>> res, int first, boolean[] used) {
        if (first == len) {
            res.add(new ArrayList<>(path));
        }
        for (int i = 0; i < len; ++i) {
            if (used[i]) {
                continue;
            }
            // 剪枝条件：i > 0 是为了保证 nums[i - 1] 有意义
            // 写 !used[i - 1] 是因为 nums[i - 1] 在深度优先遍历的过程中刚刚被撤销选择

            if (i>0 && nums[i] == nums[i-1] && !used[i - 1]) {
                continue;
            }
            path.addLast(nums[i]);
            used[i] = true;
            // use next integers to complete the permutations
            backtrack(len, nums, path, res, first + 1, used);
            used[i] = false;
            // backtrack
            path.removeLast();
        }
    }
}
// @lc code=end

