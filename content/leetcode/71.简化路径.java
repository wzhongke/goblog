import java.util.Stack;

/*
 * @lc app=leetcode.cn id=71 lang=java
 *
 * [71] 简化路径
 */

// @lc code=start
class Solution {
    public String simplifyPath(String path) {
        Stack<String> stack = new Stack<>();
        String [] paths = path.split("/");
        for (int i=0; i<paths.length; i++) {
            if (paths[i].equals("") || paths[i].equals(".")) {
                continue;
            }
            if (paths[i].equals("..")) {
                if (!stack.empty())
                    stack.pop();
                continue;
            }
            stack.add(paths[i]);
        }
        String result = "";
        while(!stack.empty()) {
            result = "/" + stack.pop() + result;
        }
        if ("".equals(result)) {
            return "/";
        }
        return result;
    }
}
// @lc code=end

