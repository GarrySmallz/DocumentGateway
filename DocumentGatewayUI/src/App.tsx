import {useState} from "react";
import {Label} from "@/components/ui/label";
import {Input} from "@/components/ui/input";
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { MessageSender } from '@/components/MessageSender'
import { AuditLogViewer } from '@/components/AuditLogViewer'
import { SystemOverview } from '@/components/SystemOverview'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'


export default function App() {
  const [apiKey, setApiKey] = useState('')
  const [partnerId, setPartnerId] = useState('partner-a')

  return (
      <div className="container mx-auto p-6">
        <h1 className="text-2xl font-bold mb-6">Document Gateway</h1>

        <div className="space-y-1.5 mb-4">
          <Label className="block text-sm font-medium mb-1">API-Key</Label>
          <Input
            type={"password"}
            value={apiKey}
            onChange={(e) => setApiKey(e.target.value)}
            placeholder="Api-Key eingeben"
            className="max-w-sm"
          />
        </div>
        <div className="space-y-1.5 mb-4">
          <Label className="block text-sm font-medium mb-1">Partner-ID</Label>
          <Select value={partnerId} onValueChange={setPartnerId} >
            <SelectTrigger className="max-w-sm">
              <SelectValue placeholder="Partner auswählen"/>
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="partner-a">Partner A</SelectItem>
              <SelectItem value="partner-b">Partner B</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <Tabs defaultValue="sender">
            <TabsList>
                <TabsTrigger value="sender">Nachricht senden</TabsTrigger>
                <TabsTrigger value="audit">Audit Log</TabsTrigger>
                <TabsTrigger value="system">System</TabsTrigger>
            </TabsList>
            <TabsContent value="sender"><MessageSender apiKey={apiKey} partnerId={partnerId} /></TabsContent>
            <TabsContent value="audit"><AuditLogViewer apiKey={apiKey} partnerId={partnerId} /></TabsContent>
            <TabsContent value={"system"}><SystemOverview /></TabsContent>
        </Tabs>
      </div>
  )
}