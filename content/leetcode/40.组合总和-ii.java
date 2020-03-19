/*
 * @lc app=leetcode.cn id=40 lang=java
 *
 * [40] 组合总和 II
 */

// @lc code=start
class Solution {
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        Arrays.sort(candidates);
        List<List<Integer>> res = new ArrayList<>();
        backtrack(candidates, target, 0, res, new ArrayDeque<>());
        return res;
    }

    public void backtrack(int [] candidates, int target, int begin, List<List<Integer>> res, Deque<Integer> path) {
        if (target == 0) {
            res.add(new ArrayList<>(path));
            return;
        }
        for (int i=begin; i<candidates.length; i++) {
            if (target - candidates[i] < 0) {
                break;
            }
            // 小剪枝
            if (i > begin && candidates[i] == candidates[i - 1]) {
                continue;
            }
            path.addLast(candidates[i]);
            backtrack(candidates, target - candidates[i], i + 1, res, path);
            path.removeLast();
        }
    }
}
// @lc code=end

