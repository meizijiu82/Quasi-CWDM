#############################################
# Sets
#############################################
set N; #network node
set Ni{N}; #the neighboring nodes of node i;
set F; #modulation formats;
set W; #wavelengths;
set E within (N cross N); #links
set LINKS{i in N,j in N:i<>j} within E;	#shortest_path 

#############################################
# Params
#############################################
#param T	integer;
param λ{s in N,d in N:s<>d} >= 200,<=1000; #节点对sd之间的流量需求
param R{i in N,j in N,f in F:i<>j} >= 0 integer; #链路ij需要信号再生器的数目
param C{F} > 0; #Quasi-CWDM光通道的信道容量. 
param Cregen{F} > 0; #信号再生器成本
param Cip{F} > 0; #IP路由端口成本
param α > 0; #权值
param Θ{m in N,n in Ni[m],i in N,j in N:m<>n and i<>j} =
(if (m,n) in LINKS[i,j] then 1 else 0) binary; 
		#当通过光路虚拟路径ij的最短路径穿越物理路径mn时，为1；否则，为0.


#############################################
# Vars
#############################################
var λn{i in N,j in N,s in N,d in N:i<>j and s<>d} >= 10; #traffic demand
var V{i in N,j in N,f in F:i<>j} >= 0 integer;	#link(i,j)上光通道的数量
var Nip{N,F} >= 0  integer;	#节点i上IP路由端口的数量
var Nregen{i in N,j in N,f in F:i<>j} >= 0 integer;	#link(i,j)上信号再生器的数量
var δ{i in N,j in N,w in W,f in F:i<>j} binary;
		 #当虚拟链路ij上的光通道使用了调制格式为f的波长w，为1；否则，为0
var O{w in W,m in N,n in Ni[m]:m<>n}  binary;
		 #当波长w在物理链路mn上被使用，为1；否则，为0
var μ{W} binary; #当波长w被使用，为1；否则，为0
var Cw >= 0 integer; #所有光纤上的最大波长数

#############################################
# Objective
#############################################
minimize Total_Cost: sum{i in N,f in F}Cip[f] * Nip[i,f] +
  sum{i in N,j in N,f in F:i<>j}Cregen[f] * Nregen[i,j,f] + α*Cw;
  
#############################################
# Constraints
#############################################
#(1)

#(2) 
subject to nodepair_TrafficDemand 
{i in N,j in N,s in N,d in N: s <> d and i <> j}:
  λn[i,j,s,d] = λn[j,i,d,s];	
#(3)
subject to traffic_demand {i in N,j in N: i <> j}:
  sum{s in N,d in N: s <> d} λn[i,j,s,d] <= sum{f in F} C[f] * V[i,j,f]; 
#(4)
subject to channel_numbers {i in N,f in F}:
  sum{j in N: i <> j} V[i,j,f] = Nip[i,f];	
#(5)
subject to regen_numbers {i in N,j in N,f in F: i <> j}:
  Nregen[i,j,f]=R[i,j,f]*V[i,j,f];	
#(6)
subject to channel_wavelength {i in N,j in N,f in F: i <> j}:
  V[i,j,f]=sum{w in W} δ[i,j,w,f];	
#(7)
subject to link_wavelength {w in W,m in N,n in Ni[m]}:
  sum{i in N,j in N,f in F: i <> j} δ[i,j,w,f]*Θ[m,n,i,j] <=1;	
#(8)
subject to wavelength {w in W,m in N,n in Ni[m]}: μ[w]>=O[w,m,n];	
#(9)
subject to link_wavelength2 {w in W,m in N,n in Ni[m]}:
  O[w,m,n]=sum{f in F,i in N,j in N: i <> j} δ[i,j,w,f]*Θ[m,n,i,j];	
#(10)
subject to max_wavalength : Cw=sum{w in W} μ[w]; 	
#(11)
subject to max_wavelength_link {m in N,n in Ni[m]}:
  Cw >= sum{f in F,w in W,i in N,j in N: i <> j} δ[i,j,w,f]*Θ[m,n,i,j];	
 