//This program runs an ABM from the paper Racial Capitalism, section 5.2(end of section, no figure), actors in two groups play a NDG
//it is assumed that there are two possible tags for powerless actors - one more flexible and one more rigid
//flexible here is operationalized with random mutations that can be imitated
//the question is whether the powerful group comes to recognize the more rigid tag and use it to discriminate

public class Racism4 {

    public static void main(String[] args) {
            /*  try{
                FileWriter outfile = new FileWriter(args[0]);
                PrintWriter out = new PrintWriter(outfile);   */
        //Initialize parameters
        //agent num  = number of agents total, agentnumA+agentnumB = agentnum, stratnum=number of strategies
        //d = disagreement point for powerless agents (group A) and D for powerful ones (group B)
        //mutate is the probability that each agent randomly mutates their strategy each round
        int agentnum = 20;
        int agentnumA= 10;
        int agentnumB = 10;
        int stratnum = 3;
        double d = 0;
        double D = 0.32;
        double mutate = 0.01;

        //xx is number of trials and xxx is length of each trial
        int xx = 2000;
        int xxx = 3000;

        //game played, H, M, L are high medium and low demands for NDG
        double H = .67;
        double M = .5;
        double L = .33;
        double[][] payoff0={{L,L,L},{M,M,d},{H,d,d}};
        double[][] payoff1={{L,L,L},{M,M,D},{H,D,D}};
        double diffA = H-d;
        double diffB = H-D;

        //variable to record data, outcomes is proportion of discriminatory interactions, flexible is the percentage of time powerful group is attending to the flexible tag, payoffP is avg payoff of group B, payoffW is avg payoff of group A
        double outcomes=0;
        double flexible=0;
        double payoffP=0;
        double payoffW=0;

        //begin simulations, xx is the number of runs
        for (int ii = 0; ii < xx; ii++) {

            //intialize agents, entries track:
            //0 - true group ID, 0 for group A and 1 for group B, does not change
            // 1- strategy against group A tag, 0,1,or 2 for L, M, H
            //2 - strategy against group B tag, 0,1,or 2 for L, M, H
            //3 - strategy for tag recognition, 0 is rigid and 1 is flexible
            //4 - flexible tag, 0 for A,  1 for B, can mutate
            int [][] agents = new int[agentnum][5];

            //randomly initialize strategies in 1 and 2
            for (int i = 0; i < agentnum; i++)
            {int dd = (int)(Math.random()*(stratnum));
                agents[i][1] = dd;}
            for (int i = 0; i < agentnum; i++)
            {int dd = (int)(Math.random()*(stratnum));
                agents[i][2] = dd;}
//initialize strategy for tag recognition, random for group B and all dependable for group A
// initialize true group ID, with As first
 //initialize flexible tags with "true" identity
            for (int i = 0; i < agentnum; i++)
            {int dd = (int)(Math.random()*(2));
                agents[i][3] = dd;
                agents[i][0] = 1;
                agents[i][4] = 1;}
            for (int i = 0; i < agentnumA; i++)
            {agents[i][3] = 0;
                agents[i][0] = 0;
                agents[i][4] = 0;
            }

            //matrix to record, for each agent, their payoff
            double []agentpayoff = new double[agentnum];

//begin runs, xxx is number of steps
            for (int iii = 0; iii < xxx; iii++) {

                //Each agent interacts with each other agent, calculate their payoffs
                //reset agent payoffs to 0
                for (int i = 0; i < agentnum; i++) {
                    agentpayoff[i]=0;}
                //run through each pairing and assign payoffs for each interaction
                for (int i = 0; i < agentnum; i++) {
                    for (int j = i+1; j < agentnum; j++) {
                        //both group A
                        if(agents[i][0]== 0)
                        {if(agents[j][0]==0)
                        {double rr = payoff0[agents[i][1]][agents[j][1]];
                            agentpayoff[i] = agentpayoff[i]+rr;
                            double pp = payoff0[agents[j][1]][agents[i][1]];
                            agentpayoff[j] = agentpayoff[j]+pp;}
                        //group A, group B, and rigid
                        else if(agents[j][0]==1 && agents[j][3]==0)
                        {double rr = payoff0[agents[i][2]][agents[j][1]];
                            agentpayoff[i] = agentpayoff[i]+rr;
                            double pp = payoff1[agents[j][1]][agents[i][2]];
                            agentpayoff[j] = agentpayoff[j]+pp;}
                        //group A, group B, and flexible
                        else
                        {int yy=agents[i][4]+1;
                            double rr = payoff0[agents[i][2]][agents[j][yy]];
                                agentpayoff[i] = agentpayoff[i]+rr;
                                double pp = payoff1[agents[j][yy]][agents[i][2]];
                                agentpayoff[j] = agentpayoff[j]+pp;}}
                        //both in group B
                        else
                        {double rr = payoff1[agents[i][2]][agents[j][2]];
                            agentpayoff[i] = agentpayoff[i]+rr;
                            double pp = payoff1[agents[j][2]][agents[i][2]];
                            agentpayoff[j] = agentpayoff[j]+pp;}}}
                //turn agent payoffs into averages
                for (int i = 0; i < agentnum; i++) {
                    agentpayoff[i]=agentpayoff[i]/(agentnum-1);}

//Now we do the imitation step, using pairwise proportional imitation, PPI
//
// for each agent, randomly match them with an agent who is in their in-group

                int b;
                for (int i = 0; i < agentnum; i++) {
                    do { b = (int) (Math.random() * (agentnum)); }
                    while (i == b || agents[i][0] != agents[b][0]);

                    //calculate probability of imitation as difference between their payoffs/total possible difference
                    double hhh = agentpayoff[b] - agentpayoff[i];
                    if(i<agentnumA)
                    {hhh= hhh/diffA;}
                    else
                    {hhh=hhh/diffB;}

                    //use random number to determine whether to imitate strategy
                    double ee = Math.random();
                    if (ee <= hhh) {
                        agents[i][1] = agents[b][1];
                        agents[i][2] = agents[b][2];
                        agents[i][3] = agents[b][3];
                        agents[i][4] = agents[b][4];}

                    //use random number to decide whether to mutate, and, if so, mutate everything but true group
                    double jj = Math.random();
                    if (jj <= mutate) {
                        int tt = (int) (Math.random() * (stratnum));
                        agents[i][1] = tt;
                        int ss = (int) (Math.random() * (stratnum));
                        agents[i][2] = ss;
                        int dd = (int) (Math.random() * (2));
                        agents[i][3] = dd;
                    int vv = (int) (Math.random() * (2));
                        agents[i][4] = vv;}}

                //group A does not mutate rigid vs flexible recognition and group B does not mutate flexible tag(though it doesn't actually matter given how I've implemented things)
                for (int i = 0; i < agentnumA; i++) {
                    agents[i][3]=0;}
            for (int i = agentnumA; i < agentnum; i++) {
                agents[i][4]=1;}

//end runs
            }

        //record outcomes - this tracks the proportion of interactions at the end of sim where we expect discrimination between groups
            double yy=0;
            for (int i = 0; i < agentnumA; i++) {
                for (int j = agentnumA; j < agentnum; j++)
                { if(agents[i][2]==0 && agents[j][1]==2)
                {yy=yy+1;}
                else if(agents[i][2]==2 && agents[j][1]==0)
                {yy=yy+1;}}}
            yy = yy/(agentnumA*agentnumB);
            yy=yy/xx;
            outcomes = outcomes+yy;

            //record flexible - tracks how often the powerful group attends to the flexible tag
            double ww=0;
            for (int j = agentnumA; j < agentnum; j++)
            { ww=ww+agents[j][3]; }
            ww = ww/agentnumB;
            ww=ww/xx;
            flexible = flexible+ww;

            //record payoffP and payoffW - this calculates avg payoff for powerful and powerless individuals at the end of simulation
 /*           double hh=0;
            //powerless first
            for (int i = 0; i < agentnumA; i++) {
                for (int j = agentnumA; j < agentnum; j++)
                { if(agents[i][0]==0)
                { if(agents[j][0]==0)
                {hh=hh+payoff0[agents[i][1]][agents[j][1]];}
                else if(agents[j][0]==1)
                {hh=hh+payoff0[agents[i][2]][agents[j][1]];} }
                else if(agents[i][0]==1)
                { if(agents[j][0]==0)
                {hh=hh+payoff0[agents[i][1]][agents[j][2]];}
                else if(agents[j][0]==1)
                {hh=hh+payoff0[agents[i][2]][agents[j][2]];} }
                }}
            hh=hh/(agentnumA*agentnumB);
            hh=hh/xx;
            payoffW=payoffW+hh;

            //powerful next
            hh=0;
            for (int i = agentnumA; i < agentnum; i++) {
                for (int j = 0; j < agentnumA; j++)
                { if(agents[i][0]==0)
                { if(agents[j][0]==0)
                {hh=hh+payoff1[agents[i][1]][agents[j][1]];}
                else if(agents[j][0]==1)
                {hh=hh+payoff1[agents[i][2]][agents[j][1]];} }
                else if(agents[i][0]==1)
                { if(agents[j][0]==0)
                {hh=hh+payoff1[agents[i][1]][agents[j][2]];}
                else if(agents[j][0]==1)
                {hh=hh+payoff1[agents[i][2]][agents[j][2]];} }
                }}
            hh=hh/(agentnumB*agentnumA);
            hh=hh/xx;
            payoffP=payoffP+hh; */


            //close simulations
        }

        System.out.println(outcomes);
        System.out.println(flexible);
        //    System.out.println(payoffW);
        //  System.out.println(payoffP);

            /*     out.close();
        }
            catch (IOException ee)
            {ee.printStackTrace();}*/

        //out brackets
    }
}




