package com.darsa.nfaweb.services.nfa;

import com.darsa.nfaweb.services.nfa.models.NFA;
import org.springframework.stereotype.Service;

import java.util.Stack;

@Service
public class NFAService {
    private boolean alpha(char c){ return c >= 'a' && c <= 'z';}
    private boolean alphabet(char c){ return alpha(c) || c == 'E';}
    private boolean regexOperator(char c){
        return c == '(' || c == ')' || c == '*' || c == '|';
    }
    private boolean validRegExChar(char c){
        return alphabet(c) || regexOperator(c);
    }
    // validRegEx() - checks if given string is a valid regular expression.
    private boolean validRegEx(String regex){
        if (regex.isEmpty())
            return false;
        for (char c: regex.toCharArray())
            if (!validRegExChar(c))
                return false;
        return true;
    }

    public NFA compile(String regex){
        if (!validRegEx(regex)){
            System.out.println("Invalid Regular Expression Input.");
            return new NFA(); // empty NFA if invalid regex
        }

        Stack<Character> operators = new Stack <Character> ();
        Stack <NFA> operands = new Stack <NFA> ();
        Stack <NFA> concat_stack = new Stack <NFA> ();
        boolean ccflag = false; // concat flag
        char op, c; // current character of string
        int para_count = 0;
        NFA nfa1, nfa2;

        for (int i = 0; i < regex.length(); i++){
            c = regex.charAt(i);
            if (alphabet(c)){
                operands.push(new NFA(c));
                if (ccflag){ // concat this w/ previous
                    operators.push('.'); // '.' used to represent concat.
                }
                else
                    ccflag = true;
            }
            else{
                if (c == ')'){
                    ccflag = false;
                    if (para_count == 0){
                        System.out.println("Error: More end parenthesis "+
                                "than beginning parenthesis");
                        System.exit(1);
                    }
                    else{ para_count--;}
                    // process stuff on stack till '('
                    while (!operators.empty() && operators.peek() != '('){
                        op = operators.pop();
                        if (op == '.'){
                            nfa2 = operands.pop();
                            nfa1 = operands.pop();
                            operands.push(nfa1.concat(nfa2));
                        }
                        else if (op == '|'){
                            nfa2 = operands.pop();

                            if(!operators.empty() &&
                                    operators.peek() == '.'){

                                concat_stack.push(operands.pop());
                                while (!operators.empty() &&
                                        operators.peek() == '.'){

                                    concat_stack.push(operands.pop());
                                    operators.pop();
                                }
                                nfa1 = concat_stack.pop().concat(concat_stack.pop());
                                while (concat_stack.size() > 0){
                                    nfa1 =  nfa1.concat(concat_stack.pop());
                                }
                            }
                            else{
                                nfa1 = operands.pop();
                            }
                            operands.push(nfa1.union( nfa2));
                        }
                    }
                }
                else if (c == '*'){
                    operands.push(operands.pop().star());
                    ccflag = true;
                }
                else if (c == '('){ // if any other operator: push
                    operators.push(c);
                    para_count++;
                }
                else if (c == '|'){
                    operators.push(c);
                    ccflag = false;
                }
            }
        }
        while (operators.size() > 0){
            if (operands.empty()){
                System.out.println("Error: imbalance in operands and "
                        + "operators");
                System.exit(1);
            }
            op = operators.pop();
            if (op == '.'){
                nfa2 = operands.pop();
                nfa1 = operands.pop();
                operands.push(nfa1.concat(nfa2));
            }
            else if (op == '|'){
                nfa2 = operands.pop();
                if( !operators.empty() && operators.peek() == '.'){
                    concat_stack.push(operands.pop());
                    while (!operators.empty() && operators.peek() == '.'){
                        concat_stack.push(operands.pop());
                        operators.pop();
                    }
                    nfa1 = concat_stack.pop().concat(concat_stack.pop());
                    while (concat_stack.size() > 0){
                        nfa1 =  nfa1.concat(concat_stack.pop());
                    }
                }
                else{
                    nfa1 = operands.pop();
                }
                operands.push(nfa1.union(nfa2));
            }
        }
        return operands.pop();
    }

}
