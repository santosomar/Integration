package org.cisecurity.ietf.sacm

import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.roster.RosterListener
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager
import org.jivesoftware.smackx.disco.packet.DiscoverInfo
import org.jivesoftware.smackx.pubsub.AccessModel
import org.jivesoftware.smackx.pubsub.ConfigureForm
import org.jivesoftware.smackx.pubsub.Item
import org.jivesoftware.smackx.pubsub.LeafNode
import org.jivesoftware.smackx.pubsub.PayloadItem
import org.jivesoftware.smackx.pubsub.PubSubManager
import org.jivesoftware.smackx.pubsub.PublishModel
import org.jivesoftware.smackx.pubsub.SimplePayload
import org.jivesoftware.smackx.xdata.packet.DataForm
import org.jxmpp.jid.Jid
import org.jxmpp.jid.impl.LocalAndDomainpartJid

class AssessmentContentGridPublisher {
    static void main(String[] args) {


        // Load properties, create connection, and log in
        println "Loading propeties..."

        Properties properties = new Properties()
        //File file = new File("/conn-amonty.properties")
        File file = new File("/conn.properties")
        println file.getAbsolutePath()
        properties.load(new FileInputStream(file))

        println "Configuring XMPP connection.."

        XMPPTCPConnectionConfiguration xmppConnConfig = XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword(properties.user, properties.password)
            .setXmppDomain(properties.xmppdomain)
            .setHost(properties.host)
            .setPort(Integer.parseInt(properties.port))
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            .build()


        println "Connecting to XMPP server as user ${properties.user}"

        AbstractXMPPConnection xmppConn = new XMPPTCPConnection(xmppConnConfig)

        xmppConn.connect().login()

        Roster roster = Roster.getInstanceFor(xmppConn)
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all)
        //roster.preApprove(new LocalAndDomainpartJid(properties.preApprove,properties.xmppdomain).asBareJid())
        RosterListener rosterListener;
        if (roster.entries.size() < 1) {
            println "No roster entries; creating new roster entry: " + properties.xmppEntity
            LocalAndDomainpartJid jid = new LocalAndDomainpartJid(properties.xmppEntity,properties.xmppdomain)
            roster.createEntry(jid.asBareJid(),properties.xmppEntity,null)
        } else {
            roster.entries.asList().each { e ->
                println e.toString()

                if (e.isSubscriptionPending()) {
                    println "Subscription is pending...can't do more."
                } else {
                    ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(xmppConn)
                    DiscoverInfo discoInfo = discoManager.discoverInfo(e.jid)
                    println(discoInfo.toString())
                }

            }

        }

//
//        LeafNode leaf
//
//        // Create node if not available
//        PubSubManager mgr = PubSubManager.getInstance(xmppConn)
//        try {
//            leaf = mgr.getLeafNode("AssessmentContent")
//        }catch(XMPPException.XMPPErrorException xmppErrorException) {
//            if ( xmppErrorException.getXMPPError().descriptiveText == null ) {
//                println "Creating node"
//                ConfigureForm form = new ConfigureForm(DataForm.Type.submit)
//                form.setAccessModel(AccessModel.open)
//                form.setDeliverPayloads(true)
//                form.setNotifyRetract(true)
//                form.setPersistentItems(true)
//                form.setPublishModel(PublishModel.open)
//                leaf = mgr.createNode("AssessmentContent",form)
//            }
//
//        }
//
//
//        def iodef = """<IODEF-Document version="2.00" xml:lang="en"
//            xmlns="urn:ietf:params:xml:ns:iodef-2.0"
//            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
//            xsi:schemaLocation=
//              "http://www.iana.org/assignments/xml-registry/
//               schema/iodef-2.0.xsd">
//            <Incident purpose="reporting" restriction="private">
//              <IncidentID name="csirt.example.com">492382</IncidentID>
//              <GenerationTime>2015-07-18T09:00:00-05:00</GenerationTime>
//              <Contact type="organization" role="creator">
//                <Email>
//                  <EmailTo>test@csirt.example.com</EmailTo>
//                </Email>
//              </Contact>
//            </Incident>
//          </IODEF-Document>"""
//
//		def sp = new SimplePayload(
//			"IODEF-Document",
//			"urn:ietf:params:xml:ns:iodef-2.0",
//			iodef)
//		def pi = new PayloadItem("8bh1g27aaaaa47fh9wk7", sp)
//
//        println "Publishing IODEF example to node"
//
//        leaf.send(pi)
//
//        println "Disconnecting from XMPP server"

        xmppConn.disconnect()

    }
}
