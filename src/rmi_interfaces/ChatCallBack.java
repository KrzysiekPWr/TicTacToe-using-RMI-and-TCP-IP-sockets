package rmi_interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatCallBack extends Remote{
    void getMsg(String msg) throws RemoteException;
}
