import {useEffect, useState} from "react";
import {type AuditEventDto, getAuditEvents} from "@/services/api.ts";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import {Badge} from "@/components/ui/badge";

export function AuditLogViewer({ apiKey }: { apiKey : string}) {
    const [events, setEvents] = useState<AuditEventDto[]>([])
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        const load = () => {
            getAuditEvents(apiKey)
                .then(setEvents)
                .catch(() => setError('Fehler beim Laden'))
        }

        load()
        const interval = setInterval(load, 1000)
        return (() => clearInterval(interval))
    }, [apiKey])

    return (
        <Card>
            <CardHeader>
                <CardTitle>Audit Log</CardTitle>
            </CardHeader>
            <CardContent>
                {error && <p className="text-red-500 mb-2">{error}</p>}
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Zeit</TableHead>
                            <TableHead>Correlation-ID</TableHead>
                            <TableHead>Partner-ID</TableHead>
                            <TableHead>Event-Typ</TableHead>
                            <TableHead>Outcome</TableHead>
                            <TableHead>Fehlercode</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {events.map(event => (
                            <TableRow key={event.id}>
                                <TableCell>{new Date(event.occurredAt).toLocaleString()}</TableCell>
                                <TableCell className="font-mono text-xs">{event.correlationId}</TableCell>
                                <TableCell>{event.partnerId}</TableCell>
                                <TableCell>{event.eventType}</TableCell>
                                <TableCell>
                                    <Badge variant={event.outcome === 'SUCCESS' ? 'default' : 'destructive'}>
                                        
                                        {event.outcome}
                                    </Badge>
                                </TableCell>
                                <TableCell>{event.errorCode ?? '-'}</TableCell>
                            </TableRow>
                            )
                        )}
                    </TableBody>
                </Table>
            </CardContent>
        </Card>
    )
}