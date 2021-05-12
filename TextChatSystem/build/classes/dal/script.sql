/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  hoang
 * Created: Mar 30, 2021
 */
use master
go
create database TextChatSystem
go
use TextChatSystem
go
create table Users(
    userID int identity(1000,1),
    userName nvarchar(50) unique not null,
	userFullName nvarchar(100) not null,
	userPassword nvarchar(50) not null,
	userIsOnline bit default 0
)

create table Messages(
	msgID int identity(1000,1),
	msgSender nvarchar(50) not null,
	msgReceiver nvarchar(50) not null,
        msgContent nvarchar(4000) not null,
	msgSendTime datetime default GETDATE(),
	msgIsRead bit default 0
)

alter table Users
add constraint PK_Users primary key (userName)

alter table Messages
add constraint PK_Messages primary key (msgID)

alter table Messages
add constraint FK_Messages_1 foreign key (msgSender) references Users(userName)

alter table Messages
add constraint FK_Messages_2 foreign key (msgReceiver) references Users(userName)
