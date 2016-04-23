package org.ogcs.okra.example.game.command.impl;

import org.ogcs.app.Session;
import org.ogcs.okra.example.game.command.AbstractCommand;
import org.ogcs.okra.example.game.conf.ServerProperties;
import org.ogcs.okra.example.game.generated.Example;
import org.ogcs.okra.example.game.generated.Gpb;
import org.ogcs.okra.example.game.generated.Gpb.Request;
import org.ogcs.okra.example.game.persistence.domain.MemRole;
import org.ogcs.okra.example.game.server.Role;

/**
 * create role command
 */
public class GAME_CREATE extends AbstractCommand {

    @Override
    public void execute(Session session, Request request) throws Exception {
        Example.MsgRegister msgRegister = Example.MsgRegister.parseFrom(request.getData());

        // TODO: Get role by account
        MemRole memRole = roleMapper.select(msgRegister.getAccount());
        if (memRole != null) {
            // Role is exist. Error
            return;
        }
        memRole = new MemRole();
        memRole.setAccount(msgRegister.getAccount());
        memRole.setName(msgRegister.getName());
        memRole.setUid(ServerProperties.id());
        memRole.setPsw(msgRegister.getPsw());
        //
        roleMapper.insert(memRole);

        // TODO: do some logic content
        Role player = new Role(session, memRole);
        // session set player.
        // The player's function disconnect()  will be invoked, When the session is inactive.
        session.setConnector(player);

        session.writeAndFlush(Gpb.Response.newBuilder()
                .setId(request.getId())
                .setData(request.getData())
                .build());
    }
}