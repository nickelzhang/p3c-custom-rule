package com.alibaba.p3c.pmd.lang.java.rule.other;

import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;

import java.util.Arrays;
import java.util.List;

/**
 * 循环中调用数据库、远程接口等检测
 * 基于名称的方法
 * @author zhangwenzhe
 */
public class ServiceInvokeInLoopRule extends AbstractAliRule {

    private static final String LINES_IN_FOR_XPATH = "//ForStatement//Statement//Block//BlockStatement//*//Expression//PrimaryExpression//PrimaryPrefix//Name";
    private static final String LINES_IN_WHILE_XPATH = "//WhileStatement//Statement//Block//BlockStatement//*//Expression//PrimaryExpression//PrimaryPrefix//Name";
    private static final String LINES_IN_DO_XPATH = "//DoStatement//Statement//Block//BlockStatement//*//Expression//PrimaryExpression//PrimaryPrefix//Name";

//    private static final List<String> FORBID_STRING = Arrays.asList("api", "service", "mapper", "update", "save", "remove", "list", "count");
    /**
     * 模版字符串
     */
    private static final List<String> FORBID_STRING = Arrays.asList("api.", "service.", "mapper.");

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        try {
            List<Node> linesNodes = node.findChildNodesWithXPath(LINES_IN_FOR_XPATH);
            linesNodes.addAll(node.findChildNodesWithXPath(LINES_IN_WHILE_XPATH));
            linesNodes.addAll(node.findChildNodesWithXPath(LINES_IN_DO_XPATH));

            if (linesNodes != null && linesNodes.size() > 0) {
                for (Node lineNode : linesNodes) {
                    String statement = lineNode.getImage();
                    String statmentLowerCase = statement.toLowerCase();
                    int forbidCount = (int) FORBID_STRING.stream().filter(statmentLowerCase::contains).count();

                    if (forbidCount > 0) {
                        addViolationWithMessage(data, lineNode,
                                "java.other.ServiceInvokeInLoopRule.violation.msg",
                                new Object[]{lineNode.getImage()});
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("ServiceInvokeInLoopRule检查出现异常");
            e.printStackTrace();
        }
        return super.visit(node, data);
    }
}
