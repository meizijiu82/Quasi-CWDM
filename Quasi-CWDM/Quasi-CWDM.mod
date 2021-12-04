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
param ��{s in N,d in N:s<>d} >= 200,<=1000; #�ڵ��sd֮�����������
param R{i in N,j in N,f in F:i<>j} >= 0 integer; #��·ij��Ҫ�ź�����������Ŀ
param C{F} > 0; #Quasi-CWDM��ͨ�����ŵ�����. 
param Cregen{F} > 0; #�ź��������ɱ�
param Cip{F} > 0; #IP·�ɶ˿ڳɱ�
param �� > 0; #Ȩֵ
param ��{m in N,n in Ni[m],i in N,j in N:m<>n and i<>j} =
(if (m,n) in LINKS[i,j] then 1 else 0) binary; 
		#��ͨ����·����·��ij�����·����Խ����·��mnʱ��Ϊ1������Ϊ0.


#############################################
# Vars
#############################################
var ��n{i in N,j in N,s in N,d in N:i<>j and s<>d} >= 10; #traffic demand
var V{i in N,j in N,f in F:i<>j} >= 0 integer;	#link(i,j)�Ϲ�ͨ��������
var Nip{N,F} >= 0  integer;	#�ڵ�i��IP·�ɶ˿ڵ�����
var Nregen{i in N,j in N,f in F:i<>j} >= 0 integer;	#link(i,j)���ź�������������
var ��{i in N,j in N,w in W,f in F:i<>j} binary;
		 #��������·ij�ϵĹ�ͨ��ʹ���˵��Ƹ�ʽΪf�Ĳ���w��Ϊ1������Ϊ0
var O{w in W,m in N,n in Ni[m]:m<>n}  binary;
		 #������w��������·mn�ϱ�ʹ�ã�Ϊ1������Ϊ0
var ��{W} binary; #������w��ʹ�ã�Ϊ1������Ϊ0
var Cw >= 0 integer; #���й����ϵ���󲨳���

#############################################
# Objective
#############################################
minimize Total_Cost: sum{i in N,f in F}Cip[f] * Nip[i,f] +
  sum{i in N,j in N,f in F:i<>j}Cregen[f] * Nregen[i,j,f] + ��*Cw;
  
#############################################
# Constraints
#############################################
#(1)

#(2) 
subject to nodepair_TrafficDemand 
{i in N,j in N,s in N,d in N: s <> d and i <> j}:
  ��n[i,j,s,d] = ��n[j,i,d,s];	
#(3)
subject to traffic_demand {i in N,j in N: i <> j}:
  sum{s in N,d in N: s <> d} ��n[i,j,s,d] <= sum{f in F} C[f] * V[i,j,f]; 
#(4)
subject to channel_numbers {i in N,f in F}:
  sum{j in N: i <> j} V[i,j,f] = Nip[i,f];	
#(5)
subject to regen_numbers {i in N,j in N,f in F: i <> j}:
  Nregen[i,j,f]=R[i,j,f]*V[i,j,f];	
#(6)
subject to channel_wavelength {i in N,j in N,f in F: i <> j}:
  V[i,j,f]=sum{w in W} ��[i,j,w,f];	
#(7)
subject to link_wavelength {w in W,m in N,n in Ni[m]}:
  sum{i in N,j in N,f in F: i <> j} ��[i,j,w,f]*��[m,n,i,j] <=1;	
#(8)
subject to wavelength {w in W,m in N,n in Ni[m]}: ��[w]>=O[w,m,n];	
#(9)
subject to link_wavelength2 {w in W,m in N,n in Ni[m]}:
  O[w,m,n]=sum{f in F,i in N,j in N: i <> j} ��[i,j,w,f]*��[m,n,i,j];	
#(10)
subject to max_wavalength : Cw=sum{w in W} ��[w]; 	
#(11)
subject to max_wavelength_link {m in N,n in Ni[m]}:
  Cw >= sum{f in F,w in W,i in N,j in N: i <> j} ��[i,j,w,f]*��[m,n,i,j];	
 