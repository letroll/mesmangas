/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\julien\\Dropbox\\Workspace\\mesmangas\\src\\fr\\letroll\\downloadservice\\Iservice.aidl
 */
package fr.letroll.downloadservice;
public interface Iservice extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements fr.letroll.downloadservice.Iservice
{
private static final java.lang.String DESCRIPTOR = "fr.letroll.downloadservice.Iservice";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an fr.letroll.downloadservice.Iservice interface,
 * generating a proxy if needed.
 */
public static fr.letroll.downloadservice.Iservice asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof fr.letroll.downloadservice.Iservice))) {
return ((fr.letroll.downloadservice.Iservice)iin);
}
return new fr.letroll.downloadservice.Iservice.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getCounter:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCounter();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_addDownload:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.addDownload(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_addDownloads:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<java.lang.String> _arg0;
_arg0 = data.createStringArrayList();
this.addDownloads(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setDestinationPath:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setDestinationPath(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setPos:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setPos(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements fr.letroll.downloadservice.Iservice
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public int getCounter() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCounter, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void addDownload(java.lang.String aString) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(aString);
mRemote.transact(Stub.TRANSACTION_addDownload, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void addDownloads(java.util.List<java.lang.String> list) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStringList(list);
mRemote.transact(Stub.TRANSACTION_addDownloads, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setDestinationPath(java.lang.String aPath) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(aPath);
mRemote.transact(Stub.TRANSACTION_setDestinationPath, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setPos(int aPos) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(aPos);
mRemote.transact(Stub.TRANSACTION_setPos, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getCounter = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_addDownload = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_addDownloads = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_setDestinationPath = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_setPos = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public int getCounter() throws android.os.RemoteException;
public void addDownload(java.lang.String aString) throws android.os.RemoteException;
public void addDownloads(java.util.List<java.lang.String> list) throws android.os.RemoteException;
public void setDestinationPath(java.lang.String aPath) throws android.os.RemoteException;
public void setPos(int aPos) throws android.os.RemoteException;
}
