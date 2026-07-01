import {useState} from "react";
import {Label} from "@/components/ui/label";
import {Input} from "@/components/ui/input";
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { MessageSender } from '@/components/MessageSender'
import { AuditLogViewer } from '@/components/AuditLogViewer'
import { SystemOverview } from '@/components/SystemOverview'


export default function App() {
  const [apiKey, setApiKey] = useState('')

  return (
      <div className="container mx-auto p-6">
        <h1 className="text-2xl font-bold mb-6">Document Gateway</h1>

        <div className="mb-6">
          <Label>API-Key</Label>
          <Input
            type={"password"}
            value={apiKey}
            onChange={(e) => setApiKey(e.target.value)}
            placeholder="Api-Key eingeben"
            className="max-w-sm"
          />
        </div>

        <Tabs defaultValue="sender">
            <TabsList>
                <TabsTrigger value="sender">Nachricht senden</TabsTrigger>
                <TabsTrigger value="audit">Audit Log</TabsTrigger>
                <TabsTrigger value="system">System</TabsTrigger>
            </TabsList>
            <TabsContent value="sender"><MessageSender /></TabsContent>
            <TabsContent value="audit"><AuditLogViewer apiKey={apiKey}/></TabsContent>
            <TabsContent value={"system"}><SystemOverview /></TabsContent>
        </Tabs>
      </div>
  )
}